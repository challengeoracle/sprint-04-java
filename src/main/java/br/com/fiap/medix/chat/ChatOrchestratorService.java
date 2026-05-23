package br.com.fiap.medix.chat;

import br.com.fiap.medix.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.fiap.medix.chat.util.ChatTextUtils.normalizar;

@Service
@RequiredArgsConstructor
public class ChatOrchestratorService {

    private final ChatAgendamentoFlowService agendamentoFlowService;
    private final ChatService chatService;

    public String responder(String sessionKey, String mensagem) {
        if (agendamentoFlowService.existeFluxoAtivo(sessionKey)) {
            return agendamentoFlowService.responderFluxoAtivo(sessionKey, mensagem);
        }

        if (pareceCancelamento(mensagem)) {
            return agendamentoFlowService.iniciarCancelamento(sessionKey, mensagem);
        }

        if (pareceListagem(mensagem)) {
            return agendamentoFlowService.iniciarListagem(sessionKey, mensagem);
        }

        if (ehPerguntaSobreProcessoDeAgendamento(mensagem)) {
            return chatService.askMedixAi(mensagem);
        }

        if (pareceAgendamento(mensagem)) {
            return agendamentoFlowService.iniciarAgendamento(sessionKey, mensagem);
        }

        return chatService.askMedixAi(mensagem);
    }

    private boolean ehPerguntaSobreProcessoDeAgendamento(String mensagem) {
        String texto = normalizar(mensagem);

        return texto.contains("como faco para agendar")
                || texto.contains("como fazer agendamento")
                || texto.contains("como faco um agendamento")
                || texto.contains("como realizar um agendamento")
                || texto.contains("como funciona o agendamento")
                || texto.contains("qual o processo de agendamento")
                || texto.contains("me explica o agendamento");
    }

    private boolean pareceAgendamento(String mensagem) {
        String texto = normalizar(mensagem);

        return texto.contains("quero agendar")
                || texto.contains("preciso agendar")
                || texto.contains("gostaria de agendar")
                || texto.contains("quero marcar")
                || texto.contains("preciso marcar")
                || texto.contains("gostaria de marcar")
                || texto.contains("marcar consulta")
                || texto.contains("agendar consulta")
                || texto.contains("fazer um agendamento")
                || texto.contains("fazer agendamento")
                || texto.contains("nova consulta")
                || texto.contains("consulta com")
                || texto.contains("marcar cardiologista")
                || texto.contains("agendar cardiologista");
    }

    private boolean pareceListagem(String mensagem) {
        String texto = normalizar(mensagem);

        return texto.contains("meus agendamentos")
                || texto.contains("minhas consultas")
                || texto.contains("meus proximos agendamentos")
                || texto.contains("proximos agendamentos")
                || texto.contains("proximas consultas")
                || texto.contains("consultas marcadas")
                || texto.contains("listar agendamentos")
                || texto.contains("ver agendamentos")
                || texto.contains("quais sao meus agendamentos")
                || texto.contains("que consultas eu tenho");
    }

    private boolean pareceCancelamento(String mensagem) {
        String texto = normalizar(mensagem);

        return texto.contains("cancelar")
                || texto.contains("cancela")
                || texto.contains("desmarcar")
                || texto.contains("remover agendamento")
                || texto.contains("tirar agendamento")
                || texto.contains("apagar agendamento")
                || texto.contains("nao vou conseguir ir");
    }
}