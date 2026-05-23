package br.com.fiap.medix.chat;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatSessionStore {

    private final Map<String, ChatAgendamentoSession> sessions = new ConcurrentHashMap<>();

    public ChatAgendamentoSession getOrCreate(String sessionKey) {
        return sessions.computeIfAbsent(sessionKey, key -> new ChatAgendamentoSession());
    }

    public ChatAgendamentoSession reset(String sessionKey) {
        ChatAgendamentoSession session = new ChatAgendamentoSession();
        sessions.put(sessionKey, session);
        return session;
    }

    public boolean hasActiveSession(String sessionKey) {
        ChatAgendamentoSession session = sessions.get(sessionKey);
        return session != null && session.getStep() != ChatStep.INICIO;
    }

    public void remove(String sessionKey) {
        sessions.remove(sessionKey);
    }
}