package br.com.fiap.medix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TB_SALA")
@Data
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numero;
    private String nome;
    private String disponibilidade;

    @ManyToOne @JoinColumn(name = "unidade_id")
    @JsonIgnore
    private UnidadeSaude unidade;
}