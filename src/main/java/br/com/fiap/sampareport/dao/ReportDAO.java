package br.com.fiap.sampareport.dao;

import br.com.fiap.sampareport.model.Report;
import br.com.fiap.sampareport.model.ReportStatus;
import br.com.fiap.sampareport.model.ReportType;
import br.com.fiap.sampareport.config.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReportDAO {

    private final ConnectionFactory connectionFactory;

    public ReportDAO(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Report cadastrar(Report report) throws SQLException {
        String sql = "INSERT INTO T_SR_REPORT (CD_REPORT, DS_TITULO, DS_DESCRICAO, DS_CATEGORIA, " +
                     "NR_LATITUDE, NR_LONGITUDE, DS_FOTO_ANTES_URL, ST_REPORT, NR_UPVOTES, DT_CRIACAO, CD_USUARIO) " +
                     "VALUES (SQ_SR_REPORT.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, 0, SYSTIMESTAMP, ?)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"CD_REPORT"})) {

            ps.setString(1, report.getTitulo());
            ps.setString(2, report.getDescricao());
            ps.setString(3, report.getCategoria().name());
            ps.setDouble(4, report.getLatitude());
            ps.setDouble(5, report.getLongitude());
            ps.setString(6, report.getFotoAntesUrl());
            ps.setString(7, ReportStatus.PENDENTE_COMUNIDADE.name()); // Sempre nasce pendente
            ps.setLong(8, report.getUsuarioId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    report.setId(rs.getLong(1));
                }
            }
            report.setStatus(ReportStatus.PENDENTE_COMUNIDADE);
            report.setQuantidadeUpvotes(0);
            return report;
        }
    }

    public Optional<Report> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM T_SR_REPORT WHERE CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearReport(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Report> listarTodos() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM T_SR_REPORT ORDER BY DT_CRIACAO DESC";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reports.add(mapearReport(rs));
            }
        }
        return reports;
    }

    public List<Report> listarPorUsuario(Long usuarioId) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM T_SR_REPORT WHERE CD_USUARIO = ? ORDER BY DT_CRIACAO DESC";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapearReport(rs));
                }
            }
        }
        return reports;
    }

    public void atualizar(Report report) throws SQLException {
        String sql = "UPDATE T_SR_REPORT SET DS_TITULO = ?, DS_DESCRICAO = ?, DS_CATEGORIA = ?, " +
                     "NR_LATITUDE = ?, NR_LONGITUDE = ?, DS_FOTO_ANTES_URL = ? " +
                     "WHERE CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, report.getTitulo());
            ps.setString(2, report.getDescricao());
            ps.setString(3, report.getCategoria().name());
            ps.setDouble(4, report.getLatitude());
            ps.setDouble(5, report.getLongitude());
            ps.setString(6, report.getFotoAntesUrl());
            ps.setLong(7, report.getId());

            ps.executeUpdate();
        }
    }
    public void atualizarStatus(Long id, ReportStatus novoStatus) throws SQLException {
        String sql = "UPDATE T_SR_REPORT SET ST_REPORT = ? WHERE CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, novoStatus.name());
            ps.setLong(2, id);

            ps.executeUpdate();
        }
    }

    public void incrementarUpvote(Long id) throws SQLException {
        String sql = "UPDATE T_SR_REPORT SET NR_UPVOTES = NR_UPVOTES + 1 WHERE CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean remover(Long id) throws SQLException {
        String sql = "DELETE FROM T_SR_REPORT WHERE CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Report mapearReport(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("DT_CRIACAO");
        return new Report(
                rs.getLong("CD_REPORT"),
                rs.getString("DS_TITULO"),
                rs.getString("DS_DESCRICAO"),
                ReportType.valueOf(rs.getString("DS_CATEGORIA")),
                rs.getDouble("NR_LATITUDE"),
                rs.getDouble("NR_LONGITUDE"),
                rs.getString("DS_FOTO_ANTES_URL"),
                ReportStatus.valueOf(rs.getString("ST_REPORT")),
                rs.getInt("NR_UPVOTES"),
                timestamp != null ? timestamp.toLocalDateTime() : null,
                rs.getLong("CD_USUARIO")
        );
    }

    // metodo para listar todos os reports que um usuário deu upvote
    public List<Report> listarUpvotesPorUsuario(Long id) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.* FROM T_SR_REPORT r " +
                     "JOIN T_SR_UPVOTE u ON r.CD_REPORT = u.CD_REPORT " +
                     "WHERE u.CD_USUARIO = ? ORDER BY r.DT_CRIACAO DESC";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapearReport(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar upvotes do usuário: " + e.getMessage(), e);
        }
        return reports;
    }
}