package br.com.fiap.medix.dto;

import br.com.fiap.medix.model.Agendamento;
import java.util.List;

public record DashboardPacienteDTO(
        String nomePaciente,
        Agendamento proximoAgendamento,
        List<Agendamento> ultimosAgendamentos,
        long totalAgendamentosRealizados,
        long totalAgendamentosCancelados
) {}