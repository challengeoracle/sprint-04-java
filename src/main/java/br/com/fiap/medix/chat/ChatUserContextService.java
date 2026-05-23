package br.com.fiap.medix.chat;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatUserContextService {

    private final Map<String, String> emailsPorSessao = new ConcurrentHashMap<>();

    public void salvarEmail(String sessionKey, String email) {
        if (sessionKey == null || email == null || email.isBlank()) {
            return;
        }

        emailsPorSessao.put(sessionKey, email.trim().toLowerCase());
    }

    public String buscarEmail(String sessionKey) {
        if (sessionKey == null) {
            return null;
        }

        return emailsPorSessao.get(sessionKey);
    }

    public void limpar(String sessionKey) {
        if (sessionKey != null) {
            emailsPorSessao.remove(sessionKey);
        }
    }
}