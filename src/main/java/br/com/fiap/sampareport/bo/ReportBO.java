package br.com.fiap.sampareport.bo;

import br.com.fiap.sampareport.dao.*;
import br.com.fiap.sampareport.dto.*;
import br.com.fiap.sampareport.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampareport.exception.RegraNegocioException;
import br.com.fiap.sampareport.model.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class ReportBO {

    private final ReportDAO reportDAO;
    private final UsuarioDAO usuarioDAO;
    private final UpvoteDAO upvoteDAO;
    private final ReportHistoryDAO reportHistoryDAO;
    private final ComentarioDAO comentarioDAO;
    private final JustificativaDAO justificativaDAO;
    private final AvaliacaoDAO avaliacaoDAO;

    public ReportBO(ReportDAO reportDAO, UsuarioDAO usuarioDAO, UpvoteDAO upvoteDAO,
                    ReportHistoryDAO reportHistoryDAO, ComentarioDAO comentarioDAO,
                    JustificativaDAO justificativaDAO, AvaliacaoDAO avaliacaoDAO) {
        this.reportDAO = reportDAO;
        this.usuarioDAO = usuarioDAO;
        this.upvoteDAO = upvoteDAO;
        this.reportHistoryDAO = reportHistoryDAO;
        this.comentarioDAO = comentarioDAO;
        this.justificativaDAO = justificativaDAO;
        this.avaliacaoDAO = avaliacaoDAO;
    }

    public Report cadastrar(ReportRequestDTO dto) throws SQLException {
        usuarioDAO.buscarPorId(dto.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autor não encontrado com ID: " + dto.usuarioId()));

        Report report = new Report();
        report.setTitulo(dto.titulo());
        report.setDescricao(dto.descricao());
        report.setCategoria(dto.categoria());
        report.setLatitude(dto.latitude());
        report.setLongitude(dto.longitude());
        report.setFotoAntesUrl(dto.fotoAntesUrl());
        report.setUsuarioId(dto.usuarioId());

        Report reportSalvo = reportDAO.cadastrar(report);

        reportHistoryDAO.inserir(new ReportHistory(reportSalvo.getId(), null, ReportStatus.PENDENTE_COMUNIDADE));

        return reportSalvo;
    }

    public Report buscarPorId(Long id) throws SQLException {
        return reportDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado com ID: " + id));
    }

    public List<Report> listarTodos() throws SQLException {
        return reportDAO.listarTodos();
    }

    public List<Report> listarPorUsuario(Long usuarioId) throws SQLException {
        usuarioDAO.buscarPorId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + usuarioId));

        return reportDAO.listarPorUsuario(usuarioId);
    }

    public Report atualizar(Long id, ReportRequestDTO dto) throws SQLException {
        Report reportExistente = buscarPorId(id);

        reportExistente.setTitulo(dto.titulo());
        reportExistente.setDescricao(dto.descricao());
        reportExistente.setCategoria(dto.categoria());
        reportExistente.setLatitude(dto.latitude());
        reportExistente.setLongitude(dto.longitude());
        reportExistente.setFotoAntesUrl(dto.fotoAntesUrl());

        reportDAO.atualizar(reportExistente);
        return reportExistente;
    }

    public void remover(Long id) throws SQLException {
        if (!reportDAO.remover(id)) {
            throw new RecursoNaoEncontradoException("Chamado não encontrado para exclusão com ID: " + id);
        }
    }

    public Report darUpvote(Long reportId, UpvoteRequestDTO dto) throws SQLException {
        Report report = buscarPorId(reportId);

        usuarioDAO.buscarPorId(dto.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário votante não encontrado com ID: " + dto.usuarioId()));

        if (upvoteDAO.jaVotou(dto.usuarioId(), reportId)) {
            throw new RegraNegocioException("Você já confirmou este chamado anteriormente.");
        }

        upvoteDAO.inserir(new Upvote(dto.usuarioId(), reportId));
        reportDAO.incrementarUpvote(reportId);

        int totalAtualizado = report.getQuantidadeUpvotes() + 1;
        report.setQuantidadeUpvotes(totalAtualizado);

        if (totalAtualizado >= 5 && report.getStatus() == ReportStatus.PENDENTE_COMUNIDADE) {
            reportDAO.atualizarStatus(reportId, ReportStatus.APROVADO_COMUNIDADE);
            reportHistoryDAO.inserir(new ReportHistory(
                    reportId,
                    ReportStatus.PENDENTE_COMUNIDADE,
                    ReportStatus.APROVADO_COMUNIDADE
            ));
            report.setStatus(ReportStatus.APROVADO_COMUNIDADE);
        }

        return report;
    }

    public Report atualizarStatus(Long reportId, StatusUpdateDTO dto) throws SQLException {
        Report report = buscarPorId(reportId);
        ReportStatus statusAnterior = report.getStatus();
        ReportStatus statusNovo = dto.status();

        if (statusAnterior == statusNovo) {
            throw new RegraNegocioException("O chamado já se encontra no status: " + statusNovo);
        }

        reportDAO.atualizarStatus(reportId, statusNovo);
        reportHistoryDAO.inserir(new ReportHistory(reportId, statusAnterior, statusNovo));

        report.setStatus(statusNovo);
        return report;
    }

    public List<ReportHistory> listarHistorico(Long reportId) throws SQLException {
        buscarPorId(reportId); // Garante 404 se o report não existir
        return reportHistoryDAO.buscarPorReportId(reportId);
    }

    public Comentario adicionarComentario(Long reportId, ComentarioRequestDTO dto) throws SQLException {
        buscarPorId(reportId);
        usuarioDAO.buscarPorId(dto.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + dto.usuarioId()));

        Comentario comentario = new Comentario(dto.texto(), dto.usuarioId(), reportId);
        return comentarioDAO.inserir(comentario);
    }

    public List<ComentarioResponseDTO> listarComentarios(Long reportId) throws SQLException {
        buscarPorId(reportId);
        return comentarioDAO.buscarPorReportId(reportId);
    }

    public Justificativa enviarJustificativa(Long reportId, JustificativaRequestDTO dto) throws SQLException {
        Report report = buscarPorId(reportId);

        Usuario admin = usuarioDAO.buscarPorId(dto.adminId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Administrador não encontrado com ID: " + dto.adminId()));

        if (admin.getPerfil() != RoleType.ROLE_ADMIN) {
            throw new RegraNegocioException("Apenas administradores da Prefeitura podem enviar justificativas.");
        }

        if (justificativaDAO.buscarPorReportId(reportId).isPresent()) {
            throw new RegraNegocioException("Este chamado já possui uma justificativa cadastrada.");
        }

        Justificativa justificativa = new Justificativa(dto.fotoDepoisUrl(), dto.notaTecnica(), dto.adminId(), reportId);
        Justificativa salva = justificativaDAO.inserir(justificativa);

        ReportStatus statusAnterior = report.getStatus();
        reportDAO.atualizarStatus(reportId, ReportStatus.EM_HOMOLOGACAO);
        reportHistoryDAO.inserir(new ReportHistory(reportId, statusAnterior, ReportStatus.EM_HOMOLOGACAO));

        return salva;
    }

    public Justificativa buscarJustificativa(Long reportId) throws SQLException {
        buscarPorId(reportId);
        return justificativaDAO.buscarPorReportId(reportId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma justificativa encontrada para o chamado ID: " + reportId));
    }

    public Avaliacao avaliarReport(Long reportId, AvaliacaoRequestDTO dto) throws SQLException {
        Report report = buscarPorId(reportId);

        if (report.getStatus() != ReportStatus.EM_HOMOLOGACAO) {
            throw new RegraNegocioException("O chamado só pode ser avaliado após a Prefeitura enviar a justificativa.");
        }

        if (!report.getUsuarioId().equals(dto.usuarioId())) {
            throw new RegraNegocioException("Apenas o cidadão autor do chamado pode avaliá-lo.");
        }

        if (avaliacaoDAO.buscarPorReportId(reportId).isPresent()) {
            throw new RegraNegocioException("Este chamado já foi avaliado.");
        }

        Avaliacao avaliacao = new Avaliacao(dto.nota(), dto.feedback(), dto.usuarioId(), reportId);
        Avaliacao salva = avaliacaoDAO.inserir(avaliacao);

        reportDAO.atualizarStatus(reportId, ReportStatus.CONCLUIDO);
        reportHistoryDAO.inserir(new ReportHistory(reportId, ReportStatus.EM_HOMOLOGACAO, ReportStatus.CONCLUIDO));

        return salva;
    }

    public Avaliacao buscarAvaliacao(Long reportId) throws SQLException {
        buscarPorId(reportId);
        return avaliacaoDAO.buscarPorReportId(reportId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma avaliação encontrada para o chamado ID: " + reportId));
    }

    public Map<String, Object> obterMetricasGerais() throws SQLException {
        return avaliacaoDAO.obterMetricasGerais();
    }

    public List<Report> listarUpvotesPorUsuario(Long id) throws SQLException {
        try {
            usuarioDAO.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));

            return reportDAO.listarUpvotesPorUsuario(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar upvotes do usuário: " + e.getMessage(), e);
        }
    }

    public List<Justificativa> listarJustificativas() {
        return justificativaDAO.listarTodos();
    }

    public List<Avaliacao> listarAvaliacoes() {
        return avaliacaoDAO.listarTodos();
    }
}