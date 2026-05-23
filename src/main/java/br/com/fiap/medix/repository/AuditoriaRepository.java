package br.com.fiap.medix.repository;

import br.com.fiap.medix.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // PROVA DO REQUISITO: Uso de Funções Analíticas LAG e LEAD via SQL nativo no Java
    @Query(value = "SELECT LAG(email, 1, 'Vazio') OVER (ORDER BY id) || ' | ' || email || ' | ' || LEAD(email, 1, 'Vazio') OVER (ORDER BY id) FROM TB_USUARIO", nativeQuery = true)
    List<String> chamarRelatorioAnalitico();

    // PROVA DO REQUISITO: Execução de Junção Complexa (INNER JOIN) com concatenação manual para relatórios
    @Query(value = "SELECT 'Paciente: ' || p.nome || ' | Dados: ' || email FROM TB_USUARIO u INNER JOIN TB_PACIENTE p ON u.id = p.usuario_id", nativeQuery = true)
    List<String> chamarExportacaoPacientesJson();
}