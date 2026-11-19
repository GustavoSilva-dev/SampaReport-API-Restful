package br.com.fiap.sampareport.dao;

import br.com.fiap.sampareport.model.Justificativa;
import br.com.fiap.sampareport.config.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JustificativaDAO {

    private final ConnectionFactory connectionFactory;

    public JustificativaDAO(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Justificativa inserir(Justificativa justificativa) throws SQLException {
        String sql = "INSERT INTO T_SR_JUSTIFY (CD_JUSTIFICATIVA, DS_FOTO_DEPOIS_URL, DS_NOTA_TECNICA, " +
                     "DT_CONCLUSAO, CD_ADMIN, CD_REPORT) " +
                     "VALUES (SQ_SR_JUSTIFY.NEXTVAL, ?, ?, SYSTIMESTAMP, ?, ?)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"CD_JUSTIFICATIVA"})) {

            ps.setString(1, justificativa.getFotoDepoisUrl());
            ps.setString(2, justificativa.getNotaTecnica());
            ps.setLong(3, justificativa.getAdminId());
            ps.setLong(4, justificativa.getReportId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    justificativa.setId(rs.getLong(1));
                }
            }
            return justificativa;
        }
    }

    // 2. READ BY REPORT (Busca a justificativa vinculada a um chamado específico)
    public Optional<Justificativa> buscarPorReportId(Long reportId) throws SQLException {
        String sql = "SELECT * FROM T_SR_JUSTIFY WHERE CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearJustificativa(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Justificativa mapearJustificativa(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("DT_CONCLUSAO");
        return new Justificativa(
                rs.getLong("CD_JUSTIFICATIVA"),
                rs.getString("DS_FOTO_DEPOIS_URL"),
                rs.getString("DS_NOTA_TECNICA"),
                timestamp != null ? timestamp.toLocalDateTime() : null,
                rs.getLong("CD_ADMIN"),
                rs.getLong("CD_REPORT")
        );
    }

    public List<Justificativa> listarTodos() {
        String sql = "SELECT * FROM T_SR_JUSTIFY ORDER BY DT_CONCLUSAO DESC";
        List<Justificativa> justificativas = new ArrayList<>();

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                justificativas.add(mapearJustificativa(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar justificativas: " + e.getMessage(), e);
        }
        return justificativas;
    }
}