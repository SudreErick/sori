package br.com.fiap.appSori.repository;

import br.com.fiap.appSori.domain.Relacionamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RelacionamentoRepository extends MongoRepository<Relacionamento, String> {

    // Busca todos os relacionamentos de um usuário específico
    List<Relacionamento> findByUsuarioId(String usuarioId);

    // Busca todos os relacionamentos de uma organização específica
    List<Relacionamento> findByOrganizacaoId(String organizacaoId);

    // Busca um relacionamento específico entre um usuário e uma organização
    Optional<Relacionamento> findByUsuarioIdAndOrganizacaoId(String usuarioId, String organizacaoId);

}
