package br.com.fiap.appSori.repository;

import br.com.fiap.appSori.domain.CheckInDiario;
import br.com.fiap.appSori.domain.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInDiarioRepository extends MongoRepository<CheckInDiario, String> {

    /**
     * Busca um check-in específico de um usuário em uma data específica.
     * Necessário para a regra de negócio: um check-in por dia.
     */
    Optional<CheckInDiario> findByUsuarioAndDataCheckin(Usuario usuario, LocalDate dataCheckin);

    /**
     * Busca o histórico de check-ins de um usuário.
     */
    List<CheckInDiario> findByUsuarioOrderByDataCheckinDesc(Usuario usuario);
}
