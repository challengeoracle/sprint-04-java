package br.com.fiap.medix.dto;

import br.com.fiap.medix.enums.TipoColaborador;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ColaboradorDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @NotNull(message = "Tipo de colaborador é obrigatório")
        TipoColaborador tipoColaborador
) {}