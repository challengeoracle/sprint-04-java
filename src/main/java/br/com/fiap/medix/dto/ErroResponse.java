package br.com.fiap.medix.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        LocalDateTime timestamp,
        List<String> detalhes
) {}