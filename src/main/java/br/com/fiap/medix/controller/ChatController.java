package br.com.fiap.medix.controller;

import br.com.fiap.medix.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askBot(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "A mensagem nao pode estar vazia."));
        }

        String response = chatService.askMedixAi(message);
        return ResponseEntity.ok(Map.of("response", response));
    }
}