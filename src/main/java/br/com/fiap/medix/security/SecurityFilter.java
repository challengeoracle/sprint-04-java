package br.com.fiap.medix.security;

import br.com.fiap.medix.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    // Intercepta cada requisição HTTP para validar o token de acesso no cabeçalho
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = this.recoverToken(request);

        if (token != null) {
            // Extrai o identificador do usuário (e-mail) após validar a assinatura do token
            var login = tokenService.validarToken(token);

            if (login != null && !login.isEmpty()) {
                // Recupera o usuário do banco de dados Oracle para verificar permissões ativas
                UserDetails user = repository.findByEmail(login).orElse(null);

                if (user != null) {
                    // Configura o contexto de autenticação do Spring com o usuário e suas Roles
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Encaminha a requisição para o próximo filtro na corrente de segurança
        filterChain.doFilter(request, response);
    }

    // Extrai o token JWT do cabeçalho Authorization removendo o prefixo Bearer
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}