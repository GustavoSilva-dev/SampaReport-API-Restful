package br.com.fiap.sampareport.controller;

import br.com.fiap.sampareport.bo.ReportBO;
import br.com.fiap.sampareport.bo.UsuarioBO;
import br.com.fiap.sampareport.dto.LoginRequestDTO;
import br.com.fiap.sampareport.dto.UsuarioRequestDTO;
import br.com.fiap.sampareport.dto.UsuarioResponseDTO;
import br.com.fiap.sampareport.model.Report;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioBO usuarioBO;
    private final ReportBO reportBO;

    public UsuarioController(UsuarioBO usuarioBO, ReportBO reportBO) {
        this.usuarioBO = usuarioBO;
        this.reportBO = reportBO;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioRequestDTO dto) throws SQLException {
        UsuarioResponseDTO criado = usuarioBO.cadastrar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) throws SQLException {
        UsuarioResponseDTO autenticado = usuarioBO.autenticar(dto);
        return ResponseEntity.ok(autenticado);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() throws SQLException {
        return ResponseEntity.ok(usuarioBO.listarTodos());
    }

    @GetMapping("/{id}/upvotes")
    public ResponseEntity<List<Report>> listarUpvotesDoUsuario(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.listarUpvotesPorUsuario(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(usuarioBO.buscarPorId(id));
    }

    @GetMapping("/{id}/reports")
    public ResponseEntity<List<Report>> listarReportsDoUsuario(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.listarPorUsuario(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid UsuarioRequestDTO dto) throws SQLException {
        return ResponseEntity.ok(usuarioBO.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) throws SQLException {
        usuarioBO.remover(id);
        return ResponseEntity.noContent().build();
    }
}