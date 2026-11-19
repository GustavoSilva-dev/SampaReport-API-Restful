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

    // Injeção dos BOs via construtor
    public UsuarioController(UsuarioBO usuarioBO, ReportBO reportBO) {
        this.usuarioBO = usuarioBO;
        this.reportBO = reportBO;
    }

    // POST /api/usuarios -> Cadastra um novo cidadão ou administrador
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioRequestDTO dto) throws SQLException {
        UsuarioResponseDTO criado = usuarioBO.cadastrar(dto);

        // Monta o cabeçalho "Location" com a URL do novo usuário (ex: /api/usuarios/1) exigido pela FIAP
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        // Devolve status HTTP 201 Created + Location + JSON do usuário (sem a senha)
        return ResponseEntity.created(location).body(criado);
    }

    // POST /api/usuarios/login -> Autentica e-mail e senha
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) throws SQLException {
        UsuarioResponseDTO autenticado = usuarioBO.autenticar(dto);
        return ResponseEntity.ok(autenticado); // Devolve HTTP 200 OK
    }

    // GET /api/usuarios -> Lista todos os usuários
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() throws SQLException {
        return ResponseEntity.ok(usuarioBO.listarTodos());
    }

    // GET /api/usuarios/{id}/upvotes -> Lista todos os upvotes do usuário
    @GetMapping("/{id}/upvotes")
    public ResponseEntity<List<Report>> listarUpvotesDoUsuario(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.listarUpvotesPorUsuario(id));
    }

    // GET /api/usuarios/{id} -> Busca um usuário específico pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(usuarioBO.buscarPorId(id));
    }

    // GET /api/usuarios/{id}/reports -> Rota aninhada obrigatória (relacionamento 1:N: lista chamados de um usuário)
    @GetMapping("/{id}/reports")
    public ResponseEntity<List<Report>> listarReportsDoUsuario(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(reportBO.listarPorUsuario(id));
    }

    // PUT /api/usuarios/{id} -> Atualiza os dados do usuário
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid UsuarioRequestDTO dto) throws SQLException {
        return ResponseEntity.ok(usuarioBO.atualizar(id, dto));
    }

    // DELETE /api/usuarios/{id} -> Remove o usuário do banco
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) throws SQLException {
        usuarioBO.remover(id);
        return ResponseEntity.noContent().build(); // Devolve HTTP 204 No Content (padrão REST para exclusão)
    }
}