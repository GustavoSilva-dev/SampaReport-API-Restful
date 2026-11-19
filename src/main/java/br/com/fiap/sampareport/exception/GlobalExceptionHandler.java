package br.com.fiap.sampareport.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroPadrao> tratarNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        ErroPadrao erro = new ErroPadrao(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroPadrao> tratarRegraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        ErroPadrao erro = new ErroPadrao(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflito de regra de negócio",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadrao> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErroPadrao erro = new ErroPadrao(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                detalhes,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroPadrao> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ErroPadrao erro = new ErroPadrao(
                LocalDateTime.now(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Método HTTP não suportado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadrao> tratarErroGenerico(Exception ex, HttpServletRequest request) {
        ErroPadrao erro = new ErroPadrao(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor",
                "Ocorreu um erro inesperado ao processar a requisição.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}