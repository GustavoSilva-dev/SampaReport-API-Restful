package br.com.fiap.sampareport.bo;

import br.com.fiap.sampareport.dao.UsuarioDAO;
import br.com.fiap.sampareport.dto.LoginRequestDTO;
import br.com.fiap.sampareport.dto.UsuarioRequestDTO;
import br.com.fiap.sampareport.dto.UsuarioResponseDTO;
import br.com.fiap.sampareport.exception.RecursoNaoEncontradoException;
import br.com.fiap.sampareport.exception.RegraNegocioException;
import br.com.fiap.sampareport.model.RoleType;
import br.com.fiap.sampareport.model.Usuario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO;

    public UsuarioBO(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) throws SQLException {
        if (usuarioDAO.buscarPorEmail(dto.email()).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com o e-mail: " + dto.email());
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(dto.senha());
        novoUsuario.setPerfil(dto.perfil() != null ? dto.perfil() : RoleType.ROLE_USER);

        usuarioDAO.cadastrar(novoUsuario);

        Usuario usuarioSalvo = usuarioDAO.buscarPorEmail(dto.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Erro ao recuperar usuário recém-cadastrado."));

        return UsuarioResponseDTO.fromEntity(usuarioSalvo);
    }

    public UsuarioResponseDTO autenticar(LoginRequestDTO dto) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorEmail(dto.email())
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha inválidos."));

        if (!usuario.getSenha().equals(dto.senha())) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }

        return UsuarioResponseDTO.fromEntity(usuario);
    }

    public UsuarioResponseDTO buscarPorId(Long id) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));

        return UsuarioResponseDTO.fromEntity(usuario);
    }

    public List<UsuarioResponseDTO> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) throws SQLException {
        Usuario usuarioExistente = usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));

        Optional<Usuario> usuarioComMesmoEmail = usuarioDAO.buscarPorEmail(dto.email());
        if (usuarioComMesmoEmail.isPresent() && !usuarioComMesmoEmail.get().getId().equals(id)) {
            throw new RegraNegocioException("Este e-mail já está em uso por outro usuário.");
        }

        usuarioExistente.setNome(dto.nome());
        usuarioExistente.setEmail(dto.email());
        usuarioExistente.setSenha(dto.senha());
        if (dto.perfil() != null) {
            usuarioExistente.setPerfil(dto.perfil());
        }

        usuarioDAO.atualizar(usuarioExistente);
        return UsuarioResponseDTO.fromEntity(usuarioExistente);
    }

    public void remover(Long id) throws SQLException {
        boolean removido = usuarioDAO.remover(id);
        if (!removido) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado para exclusão com ID: " + id);
        }
    }
}