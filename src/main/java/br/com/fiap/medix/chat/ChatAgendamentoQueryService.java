package br.com.fiap.medix.chat;

import br.com.fiap.medix.dto.AgendamentoRequest;
import br.com.fiap.medix.enums.StatusAgendamento;
import br.com.fiap.medix.enums.TipoAgendamento;
import br.com.fiap.medix.model.Agendamento;
import br.com.fiap.medix.model.Colaborador;
import br.com.fiap.medix.model.Paciente;
import br.com.fiap.medix.model.Sala;
import br.com.fiap.medix.model.UnidadeSaude;
import br.com.fiap.medix.model.Usuario;
import br.com.fiap.medix.repository.AgendamentoRepository;
import br.com.fiap.medix.repository.ColaboradorRepository;
import br.com.fiap.medix.repository.SalaRepository;
import br.com.fiap.medix.repository.UnidadeRepository;
import br.com.fiap.medix.repository.UsuarioRepository;
import br.com.fiap.medix.service.AgendamentoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import static br.com.fiap.medix.chat.util.ChatTextUtils.formatarDataHora;
import static br.com.fiap.medix.chat.util.ChatTextUtils.nomeSala;

@Service
public class ChatAgendamentoQueryService {

    private final UsuarioRepository usuarioRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final UnidadeRepository unidadeRepository;
    private final SalaRepository salaRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final AgendamentoService agendamentoService;

    public ChatAgendamentoQueryService(
            UsuarioRepository usuarioRepository,
            ColaboradorRepository colaboradorRepository,
            UnidadeRepository unidadeRepository,
            SalaRepository salaRepository,
            AgendamentoRepository agendamentoRepository,
            AgendamentoService agendamentoService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.unidadeRepository = unidadeRepository;
        this.salaRepository = salaRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.agendamentoService = agendamentoService;
    }

    public Usuario buscarPacientePorEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase()).orElse(null);

        if (usuario instanceof Paciente) {
            return usuario;
        }

        return null;
    }

    public List<Colaborador> listarMedicosOperacionais() {
        return colaboradorRepository.findAll()
                .stream()
                .filter(colaborador -> colaborador.getTipoColaborador() != null)
                .filter(colaborador -> "OPERACIONAL".equalsIgnoreCase(colaborador.getTipoColaborador().name()))
                .sorted(Comparator.comparing(Colaborador::getNome))
                .toList();
    }

    public List<UnidadeSaude> listarUnidades() {
        return unidadeRepository.findAll();
    }

    public List<Sala> buscarSalasDaUnidade(Long unidadeId) {
        return salaRepository.findAll()
                .stream()
                .filter(sala -> sala.getUnidade() != null)
                .filter(sala -> sala.getUnidade().getId().equals(unidadeId))
                .toList();
    }

    public Sala escolherPrimeiraSalaDaUnidade(Long unidadeId) {
        List<Sala> salas = buscarSalasDaUnidade(unidadeId);
        return salas.isEmpty() ? null : salas.get(0);
    }

    public List<LocalDateTime> buscarHorariosLivres(Long medicoId, LocalDate data) {
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay();

        List<LocalTime> horariosPadrao = List.of(
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                LocalTime.of(11, 0),
                LocalTime.of(13, 0),
                LocalTime.of(13, 30),
                LocalTime.of(14, 0),
                LocalTime.of(14, 30),
                LocalTime.of(15, 0),
                LocalTime.of(15, 30),
                LocalTime.of(16, 0),
                LocalTime.of(16, 30)
        );

        List<LocalDateTime> ocupados = agendamentoRepository
                .buscarAgendamentosDoMedicoNoDia(
                        medicoId,
                        inicioDia,
                        fimDia,
                        StatusAgendamento.CANCELADO
                )
                .stream()
                .map(Agendamento::getDataHoraInicio)
                .toList();

        return horariosPadrao.stream()
                .map(data::atTime)
                .filter(dataHora -> !ocupados.contains(dataHora))
                .toList();
    }

    public List<Agendamento> buscarAgendamentosFuturos(Long pacienteId) {
        return agendamentoRepository.buscarAgendamentosFuturosDoPaciente(
                pacienteId,
                LocalDateTime.now(),
                StatusAgendamento.CANCELADO
        );
    }

    public List<Agendamento> buscarAgendamentosFuturosPorData(Long pacienteId, LocalDate data) {
        return buscarAgendamentosFuturos(pacienteId)
                .stream()
                .filter(agendamento -> agendamento.getDataHoraInicio() != null)
                .filter(agendamento -> agendamento.getDataHoraInicio().toLocalDate().equals(data))
                .toList();
    }

    public void criarAgendamento(ChatAgendamentoSession session) {
        Usuario usuario = usuarioRepository.findById(session.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado."));

        TipoAgendamento tipo = TipoAgendamento.CONSULTA;

        AgendamentoRequest dto = new AgendamentoRequest(
                session.getUnidadeId(),
                session.getSalaId(),
                session.getMedicoId(),
                session.getEspecialidade(),
                session.getDataHoraInicio(),
                session.getDataHoraInicio().plusMinutes(tipo.getDuracaoMinutos()),
                tipo
        );

        agendamentoService.criarAgendamento(dto, usuario);
    }

    public boolean cancelarAgendamento(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId).orElse(null);

        if (agendamento == null) {
            return false;
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);

        return true;
    }

    public String montarListaAgendamentos(String titulo, List<Agendamento> agendamentos) {
        StringBuilder resposta = new StringBuilder(titulo).append("\n\n");

        for (int i = 0; i < agendamentos.size(); i++) {
            resposta.append(i + 1)
                    .append(". ")
                    .append(formatarAgendamento(agendamentos.get(i)))
                    .append("\n");
        }

        return resposta.toString();
    }

    public String formatarAgendamento(Agendamento agendamento) {
        String especialidade = agendamento.getEspecialidade() != null
                ? agendamento.getEspecialidade()
                : "Consulta";

        String medico = agendamento.getMedico() != null
                ? agendamento.getMedico().getNome()
                : "Médico não informado";

        String unidade = agendamento.getUnidade() != null
                ? agendamento.getUnidade().getNome()
                : "Unidade não informada";

        return "%s com %s em %s na %s".formatted(
                especialidade,
                medico,
                formatarDataHora(agendamento.getDataHoraInicio()),
                unidade
        );
    }

    public String nomeSalaSegura(Sala sala) {
        return nomeSala(sala);
    }
}