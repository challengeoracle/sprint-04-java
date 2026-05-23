package br.com.fiap.medix.ai;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class AgendamentoTools {

    @Tool(description = "Lista médicos disponíveis por especialidade, unidade e período desejado")
    public String listarMedicosDisponiveis(String especialidade, String unidade, String periodo) {
        // Aqui depois você consulta o banco de verdade
        return """
                Médicos disponíveis encontrados:

                1. Dra. Ana Souza
                Especialidade: Cardiologia
                Unidade: Paulista
                Horários: 09:00, 10:30, 14:00

                2. Dr. Carlos Lima
                Especialidade: Cardiologia
                Unidade: Paulista
                Horários: 11:00, 15:30
                """;
    }

    @Tool(description = "Cria um pré-agendamento para um paciente com médico, unidade, data e horário")
    public String criarPreAgendamento(String paciente, String medico, String unidade, String data, String horario) {
        // Aqui depois você salva na TB_AGENDAMENTO
        return """
                Pré-agendamento criado com sucesso.

                Paciente: %s
                Médico: %s
                Unidade: %s
                Data: %s
                Horário: %s
                """.formatted(paciente, medico, unidade, data, horario);
    }
}