package br.com.fiap.medix.controller;

import br.com.fiap.medix.chat.ChatOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private final ChatOrchestratorService chatOrchestratorService;

    public ChatController(ChatOrchestratorService chatOrchestratorService) {
        this.chatOrchestratorService = chatOrchestratorService;
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askBot(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String sessionId = request.getOrDefault("sessionId", "local-demo");

        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "A mensagem não pode estar vazia."));
        }

        String response = chatOrchestratorService.responder(sessionId, message);

        return ResponseEntity.ok(Map.of("response", response));
    }
}