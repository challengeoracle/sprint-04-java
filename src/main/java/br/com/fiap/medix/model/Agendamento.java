package br.com.fiap.medix.model;

import br.com.fiap.medix.enums.StatusAgendamento;
import br.com.fiap.medix.enums.TipoAgendamento;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_AGENDAMENTO")
@Data
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne @JoinColumn(name = "medico_id", nullable = false)
    private Colaborador medico; // Deve ser do tipo OPERACIONAL

    @ManyToOne @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeSaude unidade;

    @ManyToOne @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim; // Calculado no Service

    @Enumerated(EnumType.STRING)
    private TipoAgendamento tipo;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    private String especialidade;
}