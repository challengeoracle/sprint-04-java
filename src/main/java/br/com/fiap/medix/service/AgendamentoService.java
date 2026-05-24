package br.com.fiap.medix.service;

import br.com.fiap.medix.dto.AgendamentoRequest;
import br.com.fiap.medix.dto.DashboardPacienteDTO;
import br.com.fiap.medix.dto.LookupDTO;
import br.com.fiap.medix.enums.StatusAgendamento;
import br.com.fiap.medix.enums.TipoColaborador;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository repository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Transactional
    public Agendamento criarAgendamento(AgendamentoRequest dto, Usuario pacienteLogado) {
        if (!(pacienteLogado instanceof Paciente paciente)) {
            throw new RuntimeException("Apenas pacientes podem realizar agendamentos.");
        }

        LocalDateTime inicio = dto.dataHoraInicio();

        Colaborador medico = colaboradorRepository.findAvailableDoctor(dto.medicoId(), inicio)
                .orElseThrow(() -> new RuntimeException("Médico não disponível neste horário."));

        UnidadeSaude unidade = unidadeRepository.findById(dto.unidadeId())
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada."));

        Sala sala = buscarSalaDoAgendamento(dto.salaId(), unidade.getId());

        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setMedico(medico);
        agendamento.setUnidade(unidade);
        agendamento.setSala(sala);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(dto.dataHoraFim() != null ? dto.dataHoraFim() : inicio.plusMinutes(30));
        agendamento.setTipo(dto.tipo());
        agendamento.setEspecialidade(dto.especialidade());
        agendamento.setStatus(StatusAgendamento.AGENDADO);

        return repository.save(agendamento);
    }

    private Sala buscarSalaDoAgendamento(Long salaId, Long unidadeId) {
        if (salaId != null) {
            return salaRepository.findById(salaId)
                    .orElseThrow(() -> new RuntimeException("Sala não encontrada."));
        }

        return salaRepository.findFirstByUnidadeId(unidadeId)
                .orElseThrow(() -> new RuntimeException("Nenhuma sala encontrada para esta unidade."));
    }

    @Transactional
    public void atualizarStatus(Long id, StatusAgendamento novoStatus) {
        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        agendamento.setStatus(novoStatus);
        repository.save(agendamento);
    }

    public Long consultarOcupacaoUnidade(Long unidadeId) {
        return repository.calcularOcupacaoMinutos(unidadeId);
    }

    public String testarHistorico(Long id) {
        return repository.chamarHistoricoProcedure(id);
    }

    public String testarNavegacao() {
        return repository.chamarRelatorioNavegacaoProcedure();
    }

    public List<Agendamento> listarMeusAgendamentos(Usuario usuario) {
        return repository.findAllByPacienteIdOrderByDataHoraInicioDesc(usuario.getId());
    }

    public Agendamento buscarProximoAgendamento(Usuario usuario) {
        List<Agendamento> lista = repository.findNextAgendamento(
                usuario.getId(),
                LocalDateTime.now(),
                PageRequest.of(0, 1)
        );

        return lista.isEmpty() ? null : lista.get(0);
    }

    public DashboardPacienteDTO carregarDashboard(Usuario usuario) {
        List<Agendamento> todos = repository.findAllByPacienteIdOrderByDataHoraInicioDesc(usuario.getId());

        long realizados = todos.stream()
                .filter(a -> StatusAgendamento.FINALIZADO.equals(a.getStatus()))
                .count();

        long cancelados = todos.stream()
                .filter(a -> StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .count();

        String nome = usuario instanceof Paciente paciente
                ? paciente.getNome()
                : "Usuário";

        return new DashboardPacienteDTO(
                nome,
                todos.isEmpty() ? null : todos.get(0),
                todos.stream().limit(3).toList(),
                realizados,
                cancelados
        );
    }

    public List<LookupDTO.Medico> listarMedicosSimples() {
        return colaboradorRepository.findAll()
                .stream()
                .filter(c -> TipoColaborador.OPERACIONAL.equals(c.getTipoColaborador()))
                .map(m -> new LookupDTO.Medico(m.getId(), m.getNome()))
                .toList();
    }
}