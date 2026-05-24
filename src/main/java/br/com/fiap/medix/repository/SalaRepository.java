package br.com.fiap.medix.repository;

import br.com.fiap.medix.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    Optional<Sala> findFirstByUnidadeId(Long unidadeId);
}