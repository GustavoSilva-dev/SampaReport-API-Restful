package br.com.fiap.sampareport.dao;

import br.com.fiap.sampareport.model.Avaliacao;
import br.com.fiap.sampareport.config.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AvaliacaoDAO {

    private final ConnectionFactory connectionFactory;

    public AvaliacaoDAO(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Avaliacao inserir(Avaliacao avaliacao) throws SQLException {
        String sql = "INSERT INTO T_SR_GRADE (CD_AVALIACAO, NR_NOTA, DS_FEEDBACK, DT_AVALIACAO, CD_USUARIO, CD_REPORT) " +
                     "VALUES (SQ_SR_GRADE.NEXTVAL, ?, ?, SYSTIMESTAMP, ?, ?)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"CD_AVALIACAO"})) {

            ps.setInt(1, avaliacao.getNota());
            ps.setString(2, avaliacao.getFeedback());
            ps.setLong(3, avaliacao.getUsuarioId());
            ps.setLong(4, avaliacao.getReportId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    avaliacao.setId(rs.getLong(1));
                }
            }
            return avaliacao;
        }
    }

    public Optional<Avaliacao> buscarPorReportId(Long reportId) throws SQLException {
        String sql = "SELECT * FROM T_SR_GRADE WHERE CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearAvaliacao(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Map<String, Object> obterMetricasGerais() throws SQLException {
        String sql = "SELECT NVL(ROUND(AVG(NR_NOTA), 1), 0) AS MEDIA, COUNT(*) AS TOTAL FROM T_SR_GRADE";
        Map<String, Object> resultado = new HashMap<>();

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                resultado.put("generalRating", rs.getDouble("MEDIA"));
                resultado.put("totalReviews", rs.getInt("TOTAL"));
            }
        }
        return resultado;
    }

    private Avaliacao mapearAvaliacao(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("DT_AVALIACAO");
        return new Avaliacao(
                rs.getLong("CD_AVALIACAO"),
                rs.getInt("NR_NOTA"),
                rs.getString("DS_FEEDBACK"),
                timestamp != null ? timestamp.toLocalDateTime() : null,
                rs.getLong("CD_USUARIO"),
                rs.getLong("CD_REPORT")
        );
    }

    public List<Avaliacao> listarTodos() {
        String sql = "SELECT * FROM T_SR_GRADE ORDER BY DT_AVALIACAO DESC";
        List<Avaliacao> avaliacoes = new java.util.ArrayList<>();

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                avaliacoes.add(mapearAvaliacao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar avaliações: " + e.getMessage(), e);
        }
        return avaliacoes;
    }
}