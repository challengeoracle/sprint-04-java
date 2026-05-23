package br.com.fiap.medix.config;

import br.com.fiap.medix.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    // Captura erros disparados por RAISE_APPLICATION_ERROR nas Procedures Oracle
    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<ErroResponse> handleOracleProcedures(JpaSystemException ex) {
        String fullMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        String cleanMessage = "Erro no processamento do banco de dados.";

        if (fullMessage != null && fullMessage.contains("ORA-")) {
            String[] parts = fullMessage.split(":");
            cleanMessage = parts.length > 1 ? parts[1].split("\n")[0].trim() : fullMessage;
        }

        var status = HttpStatus.BAD_REQUEST;
        var body = new ErroResponse(
                status.value(),
                "Erro de Banco (PL/SQL)",
                cleanMessage,
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(status).body(body);
    }

    // Trata violações de integridade como duplicidade de CPF/Email na TB_USUARIO
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleDuplicidade(org.springframework.dao.DataIntegrityViolationException ex) {
        var status = HttpStatus.CONFLICT;
        var body = new ErroResponse(
                status.value(),
                "Conflito de Dados",
                "Este registro (E-mail ou CPF) já existe no sistema.",
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(status).body(body);
    }

    // Gerencia erros de lógica das Functions de cálculo e validação Java
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponse> handleRuntime(RuntimeException ex) {
        var status = HttpStatus.BAD_REQUEST;
        var body = new ErroResponse(
                status.value(),
                "Regra de Negócio",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(status).body(body);
    }

    // Captura tentativas de acesso sem permissão baseadas na ROLE do usuário
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleForbidden(AccessDeniedException ex) {
        var status = HttpStatus.FORBIDDEN;
        var body = new ErroResponse(
                status.value(),
                "Acesso Negado",
                "Você não tem permissão para acessar este recurso.",
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(status).body(body);
    }
}