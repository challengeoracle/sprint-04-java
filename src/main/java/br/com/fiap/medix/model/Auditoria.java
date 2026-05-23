package br.com.fiap.medix.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_AUDITORIA")
@Getter @Setter
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tabela_nome")
    private String tabelaNome;

    @Column(name = "operacao")
    private String operacao;

    @Column(name = "usuario_db")
    private String usuarioDb;

    @Column(name = "data_evento")
    private LocalDateTime dataEvento;

    @Column(name = "dados_antigos")
    private String dadosAntigos;

    @Column(name = "dados_novos")
    private String dadosNovos;
}