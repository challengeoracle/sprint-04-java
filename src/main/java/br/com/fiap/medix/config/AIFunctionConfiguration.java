package br.com.fiap.medix.config;

import br.com.fiap.medix.dto.AgendamentoRequest;
import br.com.fiap.medix.enums.TipoAgendamento;
import br.com.fiap.medix.model.Agendamento;
import br.com.fiap.medix.repository.AgendamentoRepository;
import br.com.fiap.medix.repository.ColaboradorRepository;
import br.com.fiap.medix.repository.UnidadeRepository;
import br.com.fiap.medix.repository.UsuarioRepository;
import br.com.fiap.medix.service.AgendamentoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import br.com.fiap.medix.enums.StatusAgendamento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

@Configuration
public class AIFunctionConfiguration {

    public record PacienteAIRequest(String email) {}
    public record PacienteAIResponse(Long id, String email, String role, boolean ativo) {}

    public record UnidadeAIRequest(String filtro) {}
    public record UnidadeAIResponse(Long id, String nome, String endereco) {}

    public record MedicoAIRequest(String filtro) {}
    public record MedicoAIResponse(Long id, String email, String nome, String tipoColaborador) {}

    public record HorariosDisponiveisRequest(Long medicoId, String data, String periodo) {}
    public record HorariosDisponiveisResponse(String dataHora, boolean disponivel) {}

    public record CriarAgendamentoAIRequest(
            Long pacienteId,
            Long unidadeId,
            Long salaId,
            Long medicoId,
            String especialidade,
            String dataHoraInicio,
            String tipo
    ) {}

    public record CriarAgendamentoAIResponse(boolean sucesso, String mensagem) {}

    @Bean
    @Description("Verifica se um paciente possui cadastro ativo no sistema Medix utilizando o e-mail fornecido")
    public Function<PacienteAIRequest, PacienteAIResponse> verificarPacienteSistema(
            UsuarioRepository usuarioRepository
    ) {
        return request -> {
            if (request.email() == null || request.email().isBlank()) {
                return new PacienteAIResponse(null, "", "N/A", false);
            }

            var usuarioOpt = usuarioRepository.findByEmail(request.email().trim());

            if (usuarioOpt.isEmpty()) {
                return new PacienteAIResponse(null, request.email(), "N/A", false);
            }

            var usuario = usuarioOpt.get();

            return new PacienteAIResponse(
                    usuario.getId(),
                    usuario.getEmail(),
                    usuario.getRole() != null ? usuario.getRole().name() : "PACIENTE",
                    true
            );
        };
    }

    @Bean
    @Description("Lista todas as unidades de saúde ativas do Medix com seus respectivos nomes e endereços")
    public Function<UnidadeAIRequest, List<UnidadeAIResponse>> listarUnidadesMedix(
            UnidadeRepository unidadeRepository
    ) {
        return request -> unidadeRepository.findAll()
                .stream()
                .map(unidade -> new UnidadeAIResponse(
                        unidade.getId(),
                        unidade.getNome(),
                        unidade.getEndereco()
                ))
                .toList();
    }

    @Bean
    @Description("Lista os médicos colaboradores disponíveis para atendimento no Medix")
    public Function<MedicoAIRequest, List<MedicoAIResponse>> listarMedicosMedix(
            ColaboradorRepository colaboradorRepository
    ) {
        return request -> colaboradorRepository.findAll()
                .stream()
                .map(colaborador -> new MedicoAIResponse(
                        colaborador.getId(),
                        colaborador.getEmail(),
                        colaborador.getNome(),
                        colaborador.getTipoColaborador() != null
                                ? colaborador.getTipoColaborador().name()
                                : "OPERACIONAL"
                ))
                .toList();
    }

    @Bean
    @Description("Lista horários disponíveis reais para um médico em uma data e período. A data pode ser hoje, amanhã ou no formato yyyy-MM-dd")
    public Function<HorariosDisponiveisRequest, List<HorariosDisponiveisResponse>> listarHorariosDisponiveis(
            AgendamentoRepository agendamentoRepository
    ) {
        return request -> {
            LocalDate data = parseDataFlexivel(request.data());

            String periodo = request.periodo() == null || request.periodo().isBlank()
                    ? "todos"
                    : normalizarTexto(request.periodo());

            LocalDateTime inicioDia = data.atStartOfDay();
            LocalDateTime fimDia = data.plusDays(1).atStartOfDay();

            List<LocalTime> horariosPadrao = switch (periodo) {
                case "manha" -> List.of(
                        LocalTime.of(8, 0),
                        LocalTime.of(8, 30),
                        LocalTime.of(9, 0),
                        LocalTime.of(9, 30),
                        LocalTime.of(10, 0),
                        LocalTime.of(10, 30),
                        LocalTime.of(11, 0)
                );
                case "tarde" -> List.of(
                        LocalTime.of(13, 0),
                        LocalTime.of(13, 30),
                        LocalTime.of(14, 0),
                        LocalTime.of(14, 30),
                        LocalTime.of(15, 0),
                        LocalTime.of(15, 30),
                        LocalTime.of(16, 0),
                        LocalTime.of(16, 30)
                );
                default -> List.of(
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
            };

            List<LocalDateTime> ocupados = agendamentoRepository
                    .buscarAgendamentosDoMedicoNoDia(
                            request.medicoId(),
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
                    .map(dataHora -> new HorariosDisponiveisResponse(
                            dataHora.toString(),
                            true
                    ))
                    .toList();
        };
    }

    @Bean
    @Description("Cria e grava um novo agendamento de consulta médica no banco de dados do Medix")
    public Function<CriarAgendamentoAIRequest, CriarAgendamentoAIResponse> criarAgendamentoMedix(
            AgendamentoService agendamentoService,
            UsuarioRepository usuarioRepository
    ) {
        return request -> {
            try {
                var paciente = usuarioRepository.findById(request.pacienteId())
                        .orElseThrow(() -> new RuntimeException("Paciente não encontrado no sistema."));

                LocalDateTime dataInicio = parseDataHoraFlexivel(request.dataHoraInicio());

                TipoAgendamento tipo = request.tipo() == null || request.tipo().isBlank()
                        ? TipoAgendamento.CONSULTA
                        : TipoAgendamento.valueOf(request.tipo().toUpperCase());

                AgendamentoRequest dto = new AgendamentoRequest(
                        request.unidadeId(),
                        request.salaId(),
                        request.medicoId(),
                        request.especialidade(),
                        dataInicio,
                        dataInicio.plusMinutes(tipo.getDuracaoMinutos()),
                        tipo
                );

                agendamentoService.criarAgendamento(dto, paciente);

                return new CriarAgendamentoAIResponse(
                        true,
                        "Agendamento realizado com sucesso."
                );
            } catch (Exception e) {
                return new CriarAgendamentoAIResponse(
                        false,
                        "Não foi possível criar o agendamento: " + e.getMessage()
                );
            }
        };
    }

    private LocalDate parseDataFlexivel(String valor) {
        if (valor == null || valor.isBlank()) {
            return LocalDate.now();
        }

        String texto = normalizarTexto(valor);

        return switch (texto) {
            case "hoje" -> LocalDate.now();
            case "amanha" -> LocalDate.now().plusDays(1);
            case "depois de amanha" -> LocalDate.now().plusDays(2);
            default -> LocalDate.parse(valor.trim());
        };
    }

    private LocalDateTime parseDataHoraFlexivel(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new RuntimeException("Data e horário do agendamento não foram informados.");
        }

        String texto = valor.trim();

        if (texto.equalsIgnoreCase("hoje")) {
            return LocalDate.now().atTime(9, 0);
        }

        if (normalizarTexto(texto).equals("amanha")) {
            return LocalDate.now().plusDays(1).atTime(9, 0);
        }

        return LocalDateTime.parse(texto);
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return java.text.Normalizer
                .normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase();
    }
}