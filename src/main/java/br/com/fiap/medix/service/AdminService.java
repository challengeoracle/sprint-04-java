package br.com.fiap.medix.service;

import br.com.fiap.medix.enums.Role;
import br.com.fiap.medix.model.Auditoria;
import br.com.fiap.medix.model.Colaborador;
import br.com.fiap.medix.repository.AuditoriaRepository;
import br.com.fiap.medix.repository.ColaboradorRepository;
import br.com.fiap.medix.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired private ColaboradorRepository colaboradorRepository;
    @Autowired private AuditoriaRepository auditoriaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Persiste novo colaborador aplicando criptografia e disparando Trigger de Auditoria
    public Colaborador criarColaborador(Colaborador colaborador) {
        colaborador.setSenha(passwordEncoder.encode(colaborador.getSenha()));
        colaborador.setRole(Role.COLABORADOR);
        return colaboradorRepository.save(colaborador);
    }

    // Recupera a lista completa de logs gerados pelas Triggers de Auditoria do Oracle
    public List<Auditoria> listarTodosLogs() {
        return auditoriaRepository.findAll();
    }

    // PROVA DO REQUISITO: Camada de serviço acessando a Function Oracle de conversão JSON
    public String obterUsuarioJson(Long id) {
        return usuarioRepository.getUsuarioCustomJson(id);
    }
}