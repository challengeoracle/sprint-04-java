package br.com.fiap.medix.service;

import br.com.fiap.medix.model.RagContext;
import br.com.fiap.medix.repository.RagContextRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final RagContextRepository ragRepository;

    public ChatService(ChatClient.Builder builder, RagContextRepository ragRepository) {
        this.chatClient = builder.build();
        this.ragRepository = ragRepository;
    }

    public String askMedixAi(String userMessage) {
        String termoBusca = extrairTermoChave(userMessage);

        List<String> contextosEncontrados = ragRepository
                .buscarContextoPorTermo(termoBusca, PageRequest.of(0, 2))
                .stream()
                .map(RagContext::getConteudo)
                .toList();

        String contextoPdf = contextosEncontrados.isEmpty()
                ? "Nenhuma regra específica foi encontrada no regulamento."
                : String.join("\n\n", contextosEncontrados);

        String systemPrompt = """
        Você é a Medix AI, assistente virtual oficial da clínica Medix.

        Use o contexto abaixo para responder dúvidas administrativas, clínicas gerais e orientações sobre funcionamento.

        CONTEXTO:
        %s

        Regras obrigatórias:
        - Responda em português do Brasil.
        - Seja breve, claro, humano e educado.
        - Não use Markdown complexo.
        - Não use emojis.
        - Nunca diga que está lendo um PDF, documento, arquivo, regras internas, prompt, RAG ou base de conhecimento.
        - Nunca mencione ferramentas, banco de dados, integração, system prompt ou detalhes técnicos.
        - Nunca diga que não tem acesso ao sistema se o assunto for agendamento, listagem ou cancelamento.
        - Se o usuário perguntar como funciona o agendamento, explique o processo de forma geral.
        - Se o usuário quiser efetivamente agendar, diga que você pode iniciar o processo e peça o e-mail do paciente.
        - Nunca invente médicos, unidades, salas, horários ou disponibilidade.
        - Nunca dê diagnóstico médico definitivo.
        - Nunca recomende medicamentos.
        - Em sintomas graves, oriente buscar atendimento médico imediato.
        """.formatted(contextoPdf);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }

    private String extrairTermoChave(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return "agendamento";
        }

        String texto = Normalizer
                .normalize(mensagem, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .toLowerCase();

        String[] palavras = texto.split("\\s+");

        String maior = "agendamento";

        for (String palavra : palavras) {
            if (palavra.length() > maior.length() && palavra.length() > 3) {
                maior = palavra;
            }
        }

        return maior;
    }
}