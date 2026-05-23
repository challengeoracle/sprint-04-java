package br.com.fiap.medix.security;

public record UsuarioRequest(String email, String senha, String role) {
}
