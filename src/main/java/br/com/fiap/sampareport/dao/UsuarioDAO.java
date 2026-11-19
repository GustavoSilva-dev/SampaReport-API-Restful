package br.com.fiap.sampareport.dao;

import br.com.fiap.sampareport.config.ConnectionFactory;
import br.com.fiap.sampareport.model.RoleType;
import br.com.fiap.sampareport.model.Usuario;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDAO {

    private final ConnectionFactory connectionFactory;

    public UsuarioDAO(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void cadastrar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO T_SR_USUARIO (CD_USUARIO, NM_USUARIO, DS_EMAIL, DS_SENHA, TP_PERFIL) " +
                "VALUES (SQ_SR_USUARIO.NEXTVAL, ?, ?, ?, ?)";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getPerfil().name());

            ps.executeUpdate();
        }
    }
    public Optional<Usuario> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM T_SR_USUARIO WHERE CD_USUARIO = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM T_SR_USUARIO ORDER BY CD_USUARIO";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }

    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE T_SR_USUARIO SET NM_USUARIO = ?, DS_EMAIL = ?, DS_SENHA = ?, TP_PERFIL = ? " +
                "WHERE CD_USUARIO = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getPerfil().name());
            ps.setLong(5, usuario.getId());

            ps.executeUpdate();
        }
    }

    public boolean remover(Long id) throws SQLException {
        String sql = "DELETE FROM T_SR_USUARIO WHERE CD_USUARIO = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int linhasAfetadas = ps.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("TP_CRIACAO");
        return new Usuario(
                rs.getLong("CD_USUARIO"),
                rs.getString("NM_USUARIO"),
                rs.getString("DS_EMAIL"),
                rs.getString("DS_SENHA"),
                RoleType.valueOf(rs.getString("TP_PERFIL")),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }

    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM T_SR_USUARIO WHERE DS_EMAIL = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }
}