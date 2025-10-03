package br.com.fiap.appSori.repository;

import br.com.fiap.appSori.domain.Tentativa;
import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.enums.StatusTentativa;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TentativaRepository extends MongoRepository<Tentativa, String> {
    List<Tentativa> findByUsuario(Usuario usuario);

    Optional<Tentativa> findByUsuarioAndIdAndStatus(Usuario usuario, String id, StatusTentativa status);

    // Método já existente no service (para buscar tentativas ativas)
    List<Tentativa> findByUsuarioAndTeste_IdAndStatusIn(Usuario usuario, String testeId, List<StatusTentativa> status);

    // NOVO MÉTODO: Necessário para buscar todos os testes CONCLUÍDOS de um usuário (usado pelo TesteService)
    List<Tentativa> findByUsuarioAndStatus(Usuario usuario, StatusTentativa status);
}
