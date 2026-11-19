package br.com.fiap.sampareport.controller;

import br.com.fiap.sampareport.bo.ReportBO;
import br.com.fiap.sampareport.dto.*;
import br.com.fiap.sampareport.model.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportBO reportBO;

    public ReportController(ReportBO reportBO) {
        this.reportBO = reportBO;
    }


    @PostMapping
    public ResponseEntity<Report> cadastrar(@RequestBody @Valid ReportRequestDTO dto) throws SQLException {
        Report criado = reportBO.cadastrar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.getId())
                .toUri();

        return ResponseEntity.created(location).body(criado); // HTTP 201 Created
    }

    @GetMapping
    public ResponseEntity<List<Report>> listarTodos() throws SQLException {
        return ResponseEntity.ok(reportBO.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> buscarPorId(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Report> atualizar(@PathVariable Long id,
                                            @RequestBody @Valid ReportRequestDTO dto) throws SQLException {
        return ResponseEntity.ok(reportBO.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) throws SQLException {
        reportBO.remover(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

    @PostMapping("/{id}/upvote")
    public ResponseEntity<Report> darUpvote(@PathVariable Long id,
                                            @RequestBody @Valid UpvoteRequestDTO dto) throws SQLException {
        Report atualizado = reportBO.darUpvote(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ReportHistory>> listarHistorico(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.listarHistorico(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Comentario> adicionarComentario(@PathVariable Long id,
                                                          @RequestBody @Valid ComentarioRequestDTO dto) throws SQLException {
        Comentario criado = reportBO.adicionarComentario(id, dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{comentarioId}")
                .buildAndExpand(criado.getId())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<ComentarioResponseDTO>> listarComentarios(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.listarComentarios(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Report> atualizarStatus(@PathVariable Long id,
                                                  @RequestBody @Valid StatusUpdateDTO dto) throws SQLException {
        return ResponseEntity.ok(reportBO.atualizarStatus(id, dto));
    }

    @PostMapping("/{id}/justify")
    public ResponseEntity<Justificativa> enviarJustificativa(@PathVariable Long id,
                                                             @RequestBody @Valid JustificativaRequestDTO dto) throws SQLException {
        Justificativa criada = reportBO.enviarJustificativa(id, dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping("/{id}/justify")
    public ResponseEntity<Justificativa> buscarJustificativa(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.buscarJustificativa(id));
    }

    @GetMapping("/justify")
    public ResponseEntity<List<Justificativa>> listarJustificativas() throws SQLException {
        return ResponseEntity.ok(reportBO.listarJustificativas());
    }

    @PostMapping("/{id}/grade")
    public ResponseEntity<Avaliacao> avaliarReport(@PathVariable Long id,
                                                   @RequestBody @Valid AvaliacaoRequestDTO dto) throws SQLException {
        Avaliacao criada = reportBO.avaliarReport(id, dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(location).body(criada); // HTTP 201 Created
    }

    @GetMapping("/{id}/grade")
    public ResponseEntity<Avaliacao> buscarAvaliacao(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.buscarAvaliacao(id));
    }

    @GetMapping("/grade")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoes() throws SQLException {
        return ResponseEntity.ok(reportBO.listarAvaliacoes());
    }
}