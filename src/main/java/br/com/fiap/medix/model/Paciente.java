package br.com.fiap.medix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Table(name = "TB_PACIENTE")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Paciente extends Usuario {

    private String nome;
    private String cpf;
    private String tipoSanguineo;
    private Double altura;

    @ElementCollection
    @CollectionTable(name = "TB_PACIENTE_ALERGIAS", joinColumns = @JoinColumn(name = "paciente_id"))
    @Column(name = "alergia")
    @JsonIgnore
    private List<String> alergias;
}