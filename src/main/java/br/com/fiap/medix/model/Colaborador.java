package br.com.fiap.medix.model;

import br.com.fiap.medix.enums.TipoColaborador;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_COLABORADOR")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Colaborador extends Usuario {

    private String nome;
    private String cpf;

    @Enumerated(EnumType.STRING)
    private TipoColaborador tipoColaborador;
}