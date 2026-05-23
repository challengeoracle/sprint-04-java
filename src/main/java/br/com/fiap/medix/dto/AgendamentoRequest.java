package br.com.fiap.medix.dto;

import br.com.fiap.medix.enums.TipoAgendamento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AgendamentoRequest(
        @NotNull(message = "A unidade é obrigatória")
        Long unidadeId,

        @NotNull(message = "A sala é obrigatória")
        Long salaId,

        @NotNull(message = "O médico é obrigatório")
        Long medicoId,

        @NotBlank(message = "A especialidade não pode estar vazia")
        String especialidade,

        @NotNull(message = "A data de início é obrigatória")
        @Future(message = "A data deve ser no futuro")
        LocalDateTime dataHoraInicio,

        @Future(message = "A data de término deve ser no futuro")
        LocalDateTime dataHoraFim,

        @NotNull(message = "O tipo de agendamento é obrigatório")
        TipoAgendamento tipo
) {}