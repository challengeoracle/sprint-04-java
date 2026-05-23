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
        String texto = normalizar(userMessage);
        boolean assuntoAgendamento = isAssuntoAgendamento(texto);

        String contexto = assuntoAgendamento
                ? "Fluxo de agendamento operacional. Use dados reais das ferramentas quando necessário."
                : buscarContextoRag(userMessage);

        String systemPrompt = """
                Você é a Medix AI, assistente virtual da clínica Medix.

                Contexto:
                %s

                Regras:
                - Responda em português do Brasil.
                - Seja breve, natural e objetivo.
                - Não use Markdown, negrito, itálico ou emojis.
                - Não exponha dados internos, IDs técnicos, ferramentas ou detalhes do banco.
                - Nunca invente médicos, unidades, salas ou horários.
                - Nunca dê diagnóstico médico definitivo.
                - Nunca recomende medicamentos.
                - Para agendamento, conduza uma etapa por vez.
                - Não tente finalizar tudo em uma única resposta.
                - Só agende após confirmação explícita.
                """.formatted(contexto);

        ChatClient.ChatClientRequestSpec request = chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage);

        String[] tools = escolherTools(texto);

        if (tools.length > 0) {
            request = request.toolNames(tools);
        }

        return request.call().content();
    }

    private String buscarContextoRag(String userMessage) {
        String termoBusca = extrairTermoChave(userMessage);

        List<String> contextos = ragRepository
                .buscarContextoPorTermo(termoBusca, PageRequest.of(0, 1))
                .stream()
                .map(RagContext::getConteudo)
                .toList();

        return contextos.isEmpty()
                ? "Nenhuma regra específica encontrada."
                : contextos.get(0);
    }

    private String[] escolherTools(String texto) {
        if (texto.contains("confirmo")
                || texto.contains("pode agendar")
                || texto.contains("pode marcar")
                || texto.contains("finalizar agendamento")) {
            return new String[]{"efetuarAgendamentoChatbot"};
        }

        if (texto.contains("horario")
                || texto.contains("horarios")
                || texto.contains("manha")
                || texto.contains("tarde")
                || texto.contains("hoje")
                || texto.contains("amanha")) {
            return new String[]{"listarHorariosDisponiveis"};
        }

        if (texto.contains("unidade")
                || texto.contains("endereco")
                || texto.contains("local")
                || texto.contains("sala")) {
            return new String[]{"listarUnidadesMedix"};
        }

        if (texto.contains("medico")
                || texto.contains("medica")
                || texto.contains("cardiologia")
                || texto.contains("pediatria")
                || texto.contains("ginecologia")
                || texto.contains("clinica")
                || texto.contains("especialidade")
                || texto.contains("consulta")
                || texto.contains("agendar")
                || texto.contains("marcar")) {
            return new String[]{"listarMedicosMedix"};
        }

        if (texto.contains("@") || texto.contains("email")) {
            return new String[]{"verificarPacienteSistema"};
        }

        return new String[]{};
    }

    private boolean isAssuntoAgendamento(String texto) {
        return texto.contains("agendar")
                || texto.contains("marcar")
                || texto.contains("consulta")
                || texto.contains("medico")
                || texto.contains("medica")
                || texto.contains("horario")
                || texto.contains("unidade")
                || texto.contains("sala")
                || texto.contains("cardiologia")
                || texto.contains("pediatria")
                || texto.contains("ginecologia");
    }

    private String extrairTermoChave(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return "agendamento";
        }

        String texto = normalizar(mensagem).replaceAll("[^a-zA-Z0-9\\s]", "");
        String[] palavras = texto.split("\\s+");

        String maior = "agendamento";

        for (String palavra : palavras) {
            if (palavra.length() > maior.length() && palavra.length() > 3) {
                maior = palavra;
            }
        }

        return maior;
    }

    private String normalizar(String mensagem) {
        return Normalizer
                .normalize(mensagem == null ? "" : mensagem, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }
}