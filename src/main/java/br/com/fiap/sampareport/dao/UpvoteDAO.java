package br.com.fiap.sampareport.dao;

import br.com.fiap.sampareport.model.Upvote;
import br.com.fiap.sampareport.config.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UpvoteDAO {

    private final ConnectionFactory connectionFactory;

    public UpvoteDAO(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }


    public boolean jaVotou(Long usuarioId, Long reportId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM T_SR_UPVOTE WHERE CD_USUARIO = ? AND CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, usuarioId);
            ps.setLong(2, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                   return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    public void inserir(Upvote upvote) throws SQLException {
        String sql = "INSERT INTO T_SR_UPVOTE (CD_USUARIO, CD_REPORT, DT_VOTO) VALUES (?, ?, SYSTIMESTAMP)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, upvote.getUsuarioId());
            ps.setLong(2, upvote.getReportId());

            ps.executeUpdate();
        }
    }

    public boolean remover(Long usuarioId, Long reportId) throws SQLException {
        String sql = "DELETE FROM T_SR_UPVOTE WHERE CD_USUARIO = ? AND CD_REPORT = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, usuarioId);
            ps.setLong(2, reportId);

            return ps.executeUpdate() > 0;
        }
    }
}