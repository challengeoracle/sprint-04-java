package br.com.fiap.medix.repository;

import br.com.fiap.medix.model.UnidadeSaude;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeRepository extends JpaRepository<UnidadeSaude, Long> {
}
