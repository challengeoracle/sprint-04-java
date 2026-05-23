package br.com.fiap.medix.controller;

import br.com.fiap.medix.dto.AgendamentoRequest;
import br.com.fiap.medix.dto.DashboardPacienteDTO;
import br.com.fiap.medix.dto.LookupDTO;
import br.com.fiap.medix.enums.StatusAgendamento;
import br.com.fiap.medix.model.Agendamento;
import br.com.fiap.medix.model.Usuario;
import br.com.fiap.medix.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService service;

    @PostMapping
    public ResponseEntity<Agendamento> agendar(@RequestBody AgendamentoRequest dto, @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarAgendamento(dto, logado));
    }

    @GetMapping
    public ResponseEntity<List<Agendamento>> listarAgendamentos(@AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.ok(service.listarMeusAgendamentos(logado));
    }

    @GetMapping("/proximo")
    public ResponseEntity<Agendamento> buscarProximo(@AuthenticationPrincipal Usuario logado) {
        Agendamento proximo = service.buscarProximoAgendamento(logado);
        return proximo != null ? ResponseEntity.ok(proximo) : ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.atualizarStatus(id, StatusAgendamento.CANCELADO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmar(@PathVariable Long id) {
        service.atualizarStatus(id, StatusAgendamento.CONFIRMADO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardPacienteDTO> dashboard(@AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.ok(service.carregarDashboard(logado));
    }

    // PROVA DO REQUISITO: Executa Function para cálculo de ocupação em minutos no banco
    @GetMapping("/ocupacao-unidade/{unidadeId}")
    public ResponseEntity<Long> consultarOcupacao(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(service.consultarOcupacaoUnidade(unidadeId));
    }

    // PROVA DO REQUISITO: Executa Procedure SP_GERAR_HISTORICO_PACIENTE_JSON com parâmetro OUT
    @GetMapping("/teste-historico/{id}")
    public ResponseEntity<String> testeHistorico(@PathVariable Long id) {
        return ResponseEntity.ok(service.testarHistorico(id));
    }

    // PROVA DO REQUISITO: Executa Procedure SP_ANALISE_SEQUENCIAL_CRONOLOGICA (LAG/LEAD) com parâmetro OUT
    @GetMapping("/teste-navegacao")
    public ResponseEntity<String> testeNavegacao() {
        return ResponseEntity.ok(service.testarNavegacao());
    }

    // Endpoint para o Angular carregar a lista de médicos disponíveis para agendamento
    @GetMapping("/medicos")
    public ResponseEntity<List<LookupDTO.Medico>> listarMedicosParaSelect() {
        return ResponseEntity.ok(service.listarMedicosSimples());
    }
}