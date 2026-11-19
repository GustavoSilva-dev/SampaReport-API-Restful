package br.com.fiap.sampareport.dto;

import java.time.LocalDateTime;

public record ComentarioResponseDTO(
        Long id,
        String texto,
        LocalDateTime dataCriacao,
        Long usuarioId,
        Long reportId,
        String usuarioNome
) {
}
