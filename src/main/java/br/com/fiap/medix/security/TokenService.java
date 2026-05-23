package br.com.fiap.medix.security;

import br.com.fiap.medix.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class TokenService {
    // Recupera a chave secreta definida no application.yaml para assinar o token
    @Value("${api.security.token.secret}")
    private String secret;

    // Constrói o token JWT contendo as informações do usuário e permissões de acesso
    public String gerarToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("auth-api")
                .withSubject(usuario.getEmail())
                // Adiciona a Role no Payload para facilitar a lógica de rotas no Angular
                .withClaim("role", usuario.getRole().name())
                // Define o tempo de expiração do token para 2 horas no fuso horário local
                .withExpiresAt(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")))
                .sign(algorithm);
    }

    // Verifica se o token é autêntico e recupera o e-mail do usuário logado
    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            // Retorna vazio caso o token esteja expirado ou a assinatura seja inválida
            return "";
        }
    }
}