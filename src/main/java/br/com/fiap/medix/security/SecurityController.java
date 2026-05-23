package br.com.fiap.medix.security;

import br.com.fiap.medix.enums.Role;
import br.com.fiap.medix.model.Admin;
import br.com.fiap.medix.model.Usuario;
import br.com.fiap.medix.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Realiza o cadastro de novos usuários no sistema com criptografia de senha
    @PostMapping("/registrar")
    public ResponseEntity registrar(@RequestBody @Valid UsuarioRequest data) {
        // Valida se o e-mail informado já consta na base de dados do Oracle
        if(usuarioRepository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já cadastrado");
        }

        Admin novoUsuario = new Admin();
        novoUsuario.setEmail(data.email());
        // Aplica o PasswordEncoder antes de persistir a senha na TB_USUARIO
        novoUsuario.setSenha(passwordEncoder.encode(data.senha()));
        novoUsuario.setRole(Role.valueOf(data.role().toUpperCase()));

        usuarioRepository.save(novoUsuario);

        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    // Endpoint de autenticação que gera o Token JWT para acesso aos recursos protegidos
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest data) {
        // Encapsula as credenciais recebidas em um objeto de autenticação do Spring
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        // Delega a verificação das credenciais para o AuthenticationManager
        var auth = this.authManager.authenticate(usernamePassword);

        // Gera o token de acesso após a validação bem-sucedida do usuário
        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    // Record para padronizar a resposta de sucesso contendo o token JWT
    private record TokenResponse(String token){}
}