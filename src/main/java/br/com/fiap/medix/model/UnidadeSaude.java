package br.com.fiap.medix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_UNIDADE_SAUDE")
@Getter
@Setter
@Data
public class UnidadeSaude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String endereco;

    @OneToMany(mappedBy = "unidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sala> salas = new ArrayList<>();

    public void adicionarSala(Sala sala) {
        this.salas.add(sala);
        sala.setUnidade(this);
    }
}

