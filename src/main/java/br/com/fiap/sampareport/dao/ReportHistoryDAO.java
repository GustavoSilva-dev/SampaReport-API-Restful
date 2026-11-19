package br.com.fiap.sampareport.dao;

import br.com.fiap.sampareport.model.ReportHistory;
import br.com.fiap.sampareport.model.ReportStatus;
import br.com.fiap.sampareport.config.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportHistoryDAO {

    private final ConnectionFactory connectionFactory;

    public ReportHistoryDAO(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void inserir(ReportHistory history) throws SQLException {
        String sql = "INSERT INTO T_SR_REPORT_HISTORY (CD_HISTORICO, CD_REPORT, ST_ANTERIOR, ST_NOVO, DT_ALTERACAO) " +
                     "VALUES (SQ_SR_REPORT_HISTORY.NEXTVAL, ?, ?, ?, SYSTIMESTAMP)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, history.getReportId());

            if (history.getStatusAnterior() != null) {
                ps.setString(2, history.getStatusAnterior().name());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }

            ps.setString(3, history.getStatusNovo().name());

            ps.executeUpdate();
        }
    }

    public List<ReportHistory> buscarPorReportId(Long reportId) throws SQLException {
        List<ReportHistory> historico = new ArrayList<>();
        String sql = "SELECT * FROM T_SR_REPORT_HISTORY WHERE CD_REPORT = ? ORDER BY DT_ALTERACAO DESC";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    historico.add(mapearHistorico(rs));
                }
            }
        }
        return historico;
    }

    private ReportHistory mapearHistorico(ResultSet rs) throws SQLException {
        String stAnteriorStr = rs.getString("ST_ANTERIOR");
        Timestamp timestamp = rs.getTimestamp("DT_ALTERACAO");

        return new ReportHistory(
                rs.getLong("CD_HISTORICO"),
                rs.getLong("CD_REPORT"),
                stAnteriorStr != null ? ReportStatus.valueOf(stAnteriorStr) : null,
                ReportStatus.valueOf(rs.getString("ST_NOVO")),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}