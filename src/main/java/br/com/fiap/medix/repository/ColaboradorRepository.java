package br.com.fiap.medix.repository;

import br.com.fiap.medix.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    // Busca o colaborador filtrando pelo e-mail presente na classe pai Usuario
    @Query("SELECT c FROM Colaborador c WHERE c.email = :email")
    Optional<Colaborador> findByUsuarioEmail(@Param("email") String email);

    // Localiza um colaborador específico garantindo que sua função seja operacional
    @Query("SELECT c FROM Colaborador c WHERE c.id = :id AND c.tipoColaborador = 'OPERACIONAL'")
    Optional<Colaborador> findAtivoById(@Param("id") Long id);

    // Valida a disponibilidade do médico cruzando dados com a TB_AGENDAMENTO
    @Query("SELECT c FROM Colaborador c " +
            "WHERE c.id = :id " +
            "AND c.tipoColaborador = 'OPERACIONAL' " +
            "AND c.id NOT IN (SELECT a.medico.id FROM Agendamento a WHERE a.dataHoraInicio = :data AND a.status != 'CANCELADO')")
    Optional<Colaborador> findAvailableDoctor(@Param("id") Long id, @Param("data") LocalDateTime data);
}