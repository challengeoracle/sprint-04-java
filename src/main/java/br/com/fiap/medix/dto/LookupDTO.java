package br.com.fiap.medix.dto;

// DTOs simplificados para popular campos de seleção (select) no Angular
public class LookupDTO {
    public record Unidade(Long id, String nome) {}
    public record Medico(Long id, String nome) {}
}