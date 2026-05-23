package br.com.fiap.medix.chat;

import br.com.fiap.medix.model.Agendamento;
import br.com.fiap.medix.model.Colaborador;
import br.com.fiap.medix.model.Sala;
import br.com.fiap.medix.model.UnidadeSaude;
import br.com.fiap.medix.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.fiap.medix.chat.util.ChatTextUtils.extrairEmail;
import static br.com.fiap.medix.chat.util.ChatTextUtils.extrairEspecialidade;
import static br.com.fiap.medix.chat.util.ChatTextUtils.extrairNumero;
import static br.com.fiap.medix.chat.util.ChatTextUtils.formatarDataHora;
import static br.com.fiap.medix.chat.util.ChatTextUtils.formatarHora;
import static br.com.fiap.medix.chat.util.ChatTextUtils.normalizar;
import static br.com.fiap.medix.chat.util.ChatTextUtils.parseDataFlexivel;

@Service
@RequiredArgsConstructor
public class ChatAgendamentoFlowService {

    private final ChatSessionStore sessionStore;
    private final ChatUserContextService userContextService;
    private final ChatAgendamentoQueryService queryService;

    public boolean existeFluxoAtivo(String sessionKey) {
        return sessionStore.hasActiveSession(sessionKey);
    }

    public String iniciarAgendamento(String sessionKey, String mensagem) {
        ChatAgendamentoSession session = sessionStore.reset(sessionKey);
        session.setStep(ChatStep.AGENDAMENTO_AGUARDANDO_EMAIL);

        String email = extrairEmail(mensagem);

        if (email != null) {
            return receberEmailAgendamento(sessionKey, session, email);
        }

        return "Claro, posso ajudar com o agendamento. Para começar, informe o e-mail do paciente.";
    }

    public String iniciarListagem(String sessionKey, String mensagem) {
        ChatAgendamentoSession session = sessionStore.reset(sessionKey);
        session.setStep(ChatStep.LISTAGEM_AGUARDANDO_EMAIL);

        String email = extrairEmail(mensagem);

        if (email == null) {
            email = userContextService.buscarEmail(sessionKey);
        }

        if (email != null) {
            sessionStore.remove(sessionKey);
            return listarAgendamentos(sessionKey, email);
        }

        return "Para consultar seus agendamentos, informe o e-mail do paciente.";
    }

    public String iniciarCancelamento(String sessionKey, String mensagem) {
        ChatAgendamentoSession session = sessionStore.reset(sessionKey);
        session.setStep(ChatStep.CANCELAMENTO_AGUARDANDO_EMAIL);

        String email = extrairEmail(mensagem);

        if (email == null) {
            email = userContextService.buscarEmail(sessionKey);
        }

        if (email == null) {
            return "Para cancelar um agendamento, informe o e-mail do paciente.";
        }

        return prepararCancelamento(sessionKey, session, email, mensagem);
    }

    public String responderFluxoAtivo(String sessionKey, String mensagem) {
        ChatAgendamentoSession session = sessionStore.getOrCreate(sessionKey);

        if (deveReiniciar(mensagem)) {
            sessionStore.remove(sessionKey);
            return "Tudo bem, operação reiniciada. Como posso ajudar?";
        }

        return switch (session.getStep()) {
            case INICIO -> "Como posso ajudar?";

            case AGENDAMENTO_AGUARDANDO_EMAIL -> receberEmailAgendamento(sessionKey, session, mensagem);
            case AGENDAMENTO_AGUARDANDO_ESPECIALIDADE -> receberEspecialidade(session, mensagem);
            case AGENDAMENTO_ESCOLHENDO_MEDICO -> escolherMedico(session, mensagem);
            case AGENDAMENTO_AGUARDANDO_DATA -> receberData(session, mensagem);
            case AGENDAMENTO_ESCOLHENDO_HORARIO -> escolherHorario(session, mensagem);
            case AGENDAMENTO_ESCOLHENDO_UNIDADE -> escolherUnidade(session, mensagem);
            case AGENDAMENTO_CONFIRMACAO -> confirmarAgendamento(sessionKey, session, mensagem);

            case LISTAGEM_AGUARDANDO_EMAIL -> {
                String email = extrairEmail(mensagem);

                if (email == null) {
                    email = userContextService.buscarEmail(sessionKey);
                }

                sessionStore.remove(sessionKey);
                yield listarAgendamentos(sessionKey, email);
            }

            case CANCELAMENTO_AGUARDANDO_EMAIL -> {
                String email = extrairEmail(mensagem);

                if (email == null) {
                    email = userContextService.buscarEmail(sessionKey);
                }

                yield prepararCancelamento(sessionKey, session, email, mensagem);
            }

            case CANCELAMENTO_ESCOLHENDO_AGENDAMENTO -> escolherAgendamentoParaCancelar(session, mensagem);
            case CANCELAMENTO_CONFIRMACAO -> confirmarCancelamento(sessionKey, session, mensagem);
        };
    }

    private String receberEmailAgendamento(String sessionKey, ChatAgendamentoSession session, String mensagem) {
        String email = extrairEmail(mensagem);

        if (email == null) {
            return "Informe um e-mail válido para localizar o cadastro do paciente.";
        }

        Usuario usuario = queryService.buscarPacientePorEmail(email);

        if (usuario == null) {
            return "Não encontrei um paciente com esse e-mail. Verifique o e-mail informado ou realize o cadastro antes de agendar.";
        }

        session.setPacienteId(usuario.getId());
        session.setPacienteEmail(usuario.getEmail());
        session.setStep(ChatStep.AGENDAMENTO_AGUARDANDO_ESPECIALIDADE);

        userContextService.salvarEmail(sessionKey, usuario.getEmail());

        return "Cadastro localizado. Qual especialidade você deseja agendar?";
    }

    private String receberEspecialidade(ChatAgendamentoSession session, String mensagem) {
        String especialidade = extrairEspecialidade(mensagem);

        if (especialidade == null) {
            return "Informe a especialidade desejada. Exemplo: cardiologia, pediatria, ginecologia ou clínica geral.";
        }

        session.setEspecialidade(especialidade);

        List<Colaborador> medicos = queryService.listarMedicosOperacionais();

        if (medicos.isEmpty()) {
            return "Não encontrei médicos disponíveis no momento.";
        }

        session.setStep(ChatStep.AGENDAMENTO_ESCOLHENDO_MEDICO);

        StringBuilder resposta = new StringBuilder();
        resposta.append("Encontrei estes médicos disponíveis para ")
                .append(especialidade)
                .append(":\n\n");

        for (int i = 0; i < medicos.size(); i++) {
            resposta.append(i + 1)
                    .append(". ")
                    .append(medicos.get(i).getNome())
                    .append("\n");
        }

        resposta.append("\nDigite o número do médico desejado.");

        return resposta.toString();
    }

    private String escolherMedico(ChatAgendamentoSession session, String mensagem) {
        Integer escolha = extrairNumero(mensagem);
        List<Colaborador> medicos = queryService.listarMedicosOperacionais();

        if (escolha == null || escolha < 1 || escolha > medicos.size()) {
            return "Digite o número de um médico da lista.";
        }

        Colaborador medico = medicos.get(escolha - 1);

        session.setMedicoId(medico.getId());
        session.setMedicoNome(medico.getNome());
        session.setStep(ChatStep.AGENDAMENTO_AGUARDANDO_DATA);

        return "Perfeito. Para qual dia você deseja a consulta? Pode responder hoje, amanhã, depois de amanhã ou uma data como 2026-05-25.";
    }

    private String receberData(ChatAgendamentoSession session, String mensagem) {
        LocalDate data = parseDataFlexivel(mensagem);

        if (data == null) {
            return "Não consegui entender a data. Informe como hoje, amanhã ou no formato 2026-05-25.";
        }

        List<LocalDateTime> horarios = queryService.buscarHorariosLivres(session.getMedicoId(), data);

        if (horarios.isEmpty()) {
            return "Não encontrei horários livres para esse médico nessa data. Informe outra data.";
        }

        session.setDataEscolhida(data);
        session.setStep(ChatStep.AGENDAMENTO_ESCOLHENDO_HORARIO);

        StringBuilder resposta = new StringBuilder("Horários disponíveis:\n\n");

        for (int i = 0; i < horarios.size(); i++) {
            resposta.append(i + 1)
                    .append(". ")
                    .append(formatarHora(horarios.get(i)))
                    .append("\n");
        }

        resposta.append("\nDigite o número do horário desejado.");

        return resposta.toString();
    }

    private String escolherHorario(ChatAgendamentoSession session, String mensagem) {
        Integer escolha = extrairNumero(mensagem);
        List<LocalDateTime> horarios = queryService.buscarHorariosLivres(session.getMedicoId(), session.getDataEscolhida());

        if (escolha == null || escolha < 1 || escolha > horarios.size()) {
            return "Digite o número de um horário da lista.";
        }

        session.setDataHoraInicio(horarios.get(escolha - 1));
        session.setStep(ChatStep.AGENDAMENTO_ESCOLHENDO_UNIDADE);

        List<UnidadeSaude> unidades = queryService.listarUnidades();

        if (unidades.isEmpty()) {
            return "Nenhuma unidade foi encontrada para agendamento.";
        }

        StringBuilder resposta = new StringBuilder("Escolha uma unidade:\n\n");

        for (int i = 0; i < unidades.size(); i++) {
            resposta.append(i + 1)
                    .append(". ")
                    .append(unidades.get(i).getNome())
                    .append("\n");
        }

        resposta.append("\nDigite o número da unidade desejada.");

        return resposta.toString();
    }

    private String escolherUnidade(ChatAgendamentoSession session, String mensagem) {
        Integer escolha = extrairNumero(mensagem);
        List<UnidadeSaude> unidades = queryService.listarUnidades();

        if (escolha == null || escolha < 1 || escolha > unidades.size()) {
            return "Digite o número de uma unidade da lista.";
        }

        UnidadeSaude unidade = unidades.get(escolha - 1);
        Sala sala = queryService.escolherPrimeiraSalaDaUnidade(unidade.getId());

        if (sala == null) {
            return montarListaUnidades("Essa unidade não possui salas disponíveis. Escolha outra unidade:", unidades);
        }

        session.setUnidadeId(unidade.getId());
        session.setUnidadeNome(unidade.getNome());
        session.setSalaId(sala.getId());
        session.setSalaNome(queryService.nomeSalaSegura(sala));
        session.setStep(ChatStep.AGENDAMENTO_CONFIRMACAO);

        return """
                Confirme os dados do agendamento:

                Especialidade: %s
                Médico: %s
                Data e horário: %s
                Unidade: %s

                Responda confirmo para criar o agendamento ou cancelar tudo para reiniciar.
                """.formatted(
                session.getEspecialidade(),
                session.getMedicoNome(),
                formatarDataHora(session.getDataHoraInicio()),
                session.getUnidadeNome()
        );
    }

    private String confirmarAgendamento(String sessionKey, ChatAgendamentoSession session, String mensagem) {
        if (!confirmou(mensagem)) {
            return "Agendamento ainda não confirmado. Responda confirmo para finalizar ou cancelar tudo para reiniciar.";
        }

        try {
            queryService.criarAgendamento(session);
            userContextService.salvarEmail(sessionKey, session.getPacienteEmail());
            sessionStore.remove(sessionKey);

            return "Agendamento criado com sucesso.";
        } catch (Exception e) {
            return "Não foi possível criar o agendamento: " + e.getMessage();
        }
    }

    private String prepararCancelamento(
            String sessionKey,
            ChatAgendamentoSession session,
            String email,
            String mensagem
    ) {
        if (email == null) {
            return "Para cancelar um agendamento, informe o e-mail do paciente.";
        }

        Usuario usuario = queryService.buscarPacientePorEmail(email);

        if (usuario == null) {
            return "Não encontrei um paciente com esse e-mail.";
        }

        session.setPacienteId(usuario.getId());
        session.setPacienteEmail(usuario.getEmail());
        userContextService.salvarEmail(sessionKey, usuario.getEmail());

        LocalDate dataSolicitada = parseDataFlexivel(mensagem);
        List<Agendamento> agendamentos = dataSolicitada == null
                ? queryService.buscarAgendamentosFuturos(usuario.getId())
                : queryService.buscarAgendamentosFuturosPorData(usuario.getId(), dataSolicitada);

        if (agendamentos.isEmpty()) {
            sessionStore.remove(sessionKey);
            return "Você não possui agendamentos futuros para cancelar.";
        }

        if (agendamentos.size() == 1) {
            Agendamento agendamento = agendamentos.get(0);
            session.setAgendamentoIdParaCancelar(agendamento.getId());
            session.setStep(ChatStep.CANCELAMENTO_CONFIRMACAO);

            return """
                    Encontrei este agendamento:

                    %s

                    Responda confirmo para cancelar ou cancelar tudo para sair.
                    """.formatted(queryService.formatarAgendamento(agendamento));
        }

        session.setStep(ChatStep.CANCELAMENTO_ESCOLHENDO_AGENDAMENTO);

        return queryService.montarListaAgendamentos(
                "Encontrei estes agendamentos futuros. Digite o número do agendamento que deseja cancelar:",
                agendamentos
        );
    }

    private String escolherAgendamentoParaCancelar(ChatAgendamentoSession session, String mensagem) {
        Integer escolha = extrairNumero(mensagem);
        List<Agendamento> agendamentos = queryService.buscarAgendamentosFuturos(session.getPacienteId());

        if (escolha == null || escolha < 1 || escolha > agendamentos.size()) {
            return "Digite o número de um agendamento da lista.";
        }

        Agendamento agendamento = agendamentos.get(escolha - 1);

        session.setAgendamentoIdParaCancelar(agendamento.getId());
        session.setStep(ChatStep.CANCELAMENTO_CONFIRMACAO);

        return """
                Confirma o cancelamento deste agendamento?

                %s

                Responda confirmo para cancelar ou cancelar tudo para sair.
                """.formatted(queryService.formatarAgendamento(agendamento));
    }

    private String confirmarCancelamento(String sessionKey, ChatAgendamentoSession session, String mensagem) {
        if (!confirmou(mensagem)) {
            return "Cancelamento ainda não confirmado. Responda confirmo para cancelar ou cancelar tudo para sair.";
        }

        boolean cancelado = queryService.cancelarAgendamento(session.getAgendamentoIdParaCancelar());
        sessionStore.remove(sessionKey);

        return cancelado
                ? "Agendamento cancelado com sucesso."
                : "Não encontrei esse agendamento para cancelar.";
    }

    private String listarAgendamentos(String sessionKey, String email) {
        if (email == null || !email.contains("@")) {
            return "Informe o e-mail do paciente para consultar os agendamentos.";
        }

        Usuario usuario = queryService.buscarPacientePorEmail(email);

        if (usuario == null) {
            return "Não encontrei um paciente com esse e-mail.";
        }

        userContextService.salvarEmail(sessionKey, usuario.getEmail());

        List<Agendamento> agendamentos = queryService.buscarAgendamentosFuturos(usuario.getId());

        if (agendamentos.isEmpty()) {
            return "Você não possui agendamentos futuros.";
        }

        return queryService.montarListaAgendamentos("Seus agendamentos futuros:", agendamentos);
    }

    private String montarListaUnidades(String titulo, List<UnidadeSaude> unidades) {
        StringBuilder resposta = new StringBuilder(titulo).append("\n\n");

        for (int i = 0; i < unidades.size(); i++) {
            resposta.append(i + 1)
                    .append(". ")
                    .append(unidades.get(i).getNome())
                    .append("\n");
        }

        resposta.append("\nDigite o número da unidade desejada.");

        return resposta.toString();
    }

    private boolean confirmou(String mensagem) {
        String texto = normalizar(mensagem);

        return texto.contains("confirmo")
                || texto.equals("sim")
                || texto.contains("pode agendar")
                || texto.contains("pode marcar")
                || texto.contains("pode cancelar");
    }

    private boolean deveReiniciar(String mensagem) {
        String texto = normalizar(mensagem);

        return texto.contains("cancelar tudo")
                || texto.contains("reiniciar")
                || texto.contains("comecar de novo")
                || texto.contains("começar de novo");
    }
}