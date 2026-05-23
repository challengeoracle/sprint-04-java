package br.com.fiap.medix.repository;

import br.com.fiap.medix.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {
}
