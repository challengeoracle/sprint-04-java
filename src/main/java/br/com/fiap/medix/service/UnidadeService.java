package br.com.fiap.medix.service;

import br.com.fiap.medix.dto.LookupDTO;
import br.com.fiap.medix.enums.TipoColaborador;
import br.com.fiap.medix.model.Colaborador;
import br.com.fiap.medix.model.UnidadeSaude;
import br.com.fiap.medix.model.Usuario;
import br.com.fiap.medix.repository.UnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UnidadeService {

    @Autowired
    private UnidadeRepository repository;

    // Retorna a listagem de todas as unidades de saúde persistidas na TB_UNIDADE_SAUDE
    public List<UnidadeSaude> listarTodas() {
        return repository.findAll();
    }

    // Recupera uma unidade pelo ID ou lança exceção caso o registro não exista no banco
    public UnidadeSaude buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidade de Saúde não encontrada com o ID: " + id));
    }

    // Salva nova unidade e dispara Trigger de Auditoria para registrar a inserção
    @Transactional
    public UnidadeSaude salvar(UnidadeSaude unidade, Usuario logado) {
        validarAcessoAdministrativo(logado);

        // Estabelece o relacionamento bidirecional entre Sala e Unidade para integridade da FK
        if (unidade.getSalas() != null) {
            unidade.getSalas().forEach(sala -> sala.setUnidade(unidade));
        }

        return repository.save(unidade);
    }

    // Atualiza dados da unidade e registra a modificação automaticamente via Trigger
    @Transactional
    public UnidadeSaude atualizar(Long id, UnidadeSaude dadosNovos, Usuario logado) {
        validarAcessoAdministrativo(logado);

        UnidadeSaude unidadeExistente = buscarPorId(id);

        unidadeExistente.setNome(dadosNovos.getNome());
        unidadeExistente.setEndereco(dadosNovos.getEndereco());

        // Sincroniza a lista de salas removendo as antigas e vinculando as novas instâncias
        if (dadosNovos.getSalas() != null) {
            unidadeExistente.getSalas().clear();
            dadosNovos.getSalas().forEach(sala -> {
                sala.setUnidade(unidadeExistente);
                unidadeExistente.getSalas().add(sala);
            });
        }

        return repository.save(unidadeExistente);
    }

    // Remove o registro da unidade e gera log de exclusão na TB_AUDITORIA
    @Transactional
    public void excluir(Long id, Usuario logado) {
        validarAcessoAdministrativo(logado);
        UnidadeSaude unidade = buscarPorId(id);
        repository.delete(unidade);
    }

    // Valida se o usuário possui a permissão administrativa necessária para gerir unidades
    private void validarAcessoAdministrativo(Usuario usuario) {
        if (usuario instanceof Colaborador colab) {
            if (colab.getTipoColaborador() != TipoColaborador.ADMINISTRATIVO) {
                throw new AccessDeniedException("Acesso permitido apenas para colaboradores ADMINISTRATIVOS.");
            }
        } else {
            throw new AccessDeniedException("Acesso negado. Apenas colaboradores podem gerenciar unidades.");
        }
    }

    // Retorna lista simplificada de unidades para o menu select do Angular
    public List<LookupDTO.Unidade> listarUnidadesSimples() {
        return repository.findAll().stream()
                .map(u -> new LookupDTO.Unidade(u.getId(), u.getNome()))
                .toList();
    }
}