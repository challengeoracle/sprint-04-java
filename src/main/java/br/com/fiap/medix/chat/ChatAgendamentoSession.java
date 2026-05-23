package br.com.fiap.medix.chat;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatAgendamentoSession {

    private Long pacienteId;
    private String pacienteEmail;

    private String especialidade;

    private Long medicoId;
    private String medicoNome;

    private LocalDate dataEscolhida;
    private LocalDateTime dataHoraInicio;

    private Long unidadeId;
    private String unidadeNome;

    private Long salaId;
    private String salaNome;

    private Long agendamentoIdParaCancelar;

    private ChatStep step = ChatStep.INICIO;
}