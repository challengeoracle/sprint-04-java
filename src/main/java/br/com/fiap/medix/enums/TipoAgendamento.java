package br.com.fiap.medix.enums;

import lombok.Getter;

@Getter
public enum TipoAgendamento {
    CONSULTA(30),  // 30 minutos
    EXAME(60),     // 1 hora
    CIRURGIA(180); // 3 horas

    private final int duracaoMinutos;

    TipoAgendamento(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

}