package br.com.fiap.medix.repository;

import br.com.fiap.medix.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca as credenciais de acesso do usuário para o processo de autenticação
    Optional<Usuario> findByEmail(String email);

    // PROVA DO REQUISITO: Executa Function Oracle que formata dados da TB_USUARIO em JSON
    @Query(value = "SELECT FN_CONVERTE_USUARIO_JSON(:id) FROM DUAL", nativeQuery = true)
    String getUsuarioCustomJson(@Param("id") Long id);
}