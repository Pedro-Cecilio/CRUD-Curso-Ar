package com.dbserver.crud_curso.infra.excecao;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.dbserver.crud_curso.infra.excecao.novas.CriarJwtExeption;
import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Arrays;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaErro> handleErrorConstraintViolation(MethodArgumentNotValidException e) {
        BindingResult resultado = e.getBindingResult();
        String resposta = resultado.getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new RespostaErro(resposta));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RespostaErro> handleErrorBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RespostaErro(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespostaErro> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RespostaErro(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RespostaErro> handleErrorAuthentication(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RespostaErro(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespostaErro> handleErrorAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RespostaErro(e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RespostaErro> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RespostaErro(e.getMessage()));
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<RespostaErro> handlePSQLExceptionn(PSQLException e) {
        List<String> mensagemDeErro = Arrays.asList(e.getMessage().split("Detalhe:"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RespostaErro(mensagemDeErro.get(1).trim()));
    }

    @ExceptionHandler(CriarJwtExeption.class)
    public ResponseEntity<RespostaErro> handleValidarJwtExeption(CriarJwtExeption e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RespostaErro(e.getMessage()));
    }

}