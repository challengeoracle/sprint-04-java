package br.com.fiap.medix.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TB_MEDIX_RAG_CONTEXT")
@Data
public class RagContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "CONTEUDO", nullable = false)
    private String conteudo;
}