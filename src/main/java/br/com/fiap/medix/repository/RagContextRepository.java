package br.com.fiap.medix.repository;

import br.com.fiap.medix.model.RagContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RagContextRepository extends JpaRepository<RagContext, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM TB_MEDIX_RAG_CONTEXT
                    WHERE DBMS_LOB.INSTR(LOWER(CONTEUDO), LOWER(:termo)) > 0
                    """,
            nativeQuery = true
    )
    List<RagContext> buscarContextoPorTermo(@Param("termo") String termo, Pageable pageable);
}