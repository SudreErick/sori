package br.com.fiap.appSori.repository;

import br.com.fiap.appSori.domain.Teste;
import br.com.fiap.appSori.domain.enums.TipoTeste;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TesteRepository  extends MongoRepository<Teste, String> {
    // Busca por Tipo
    List<Teste> findByTipo(TipoTeste tipo);

    // Busca testes pelo status de ativo/inativo (suporta a nova regra de 'disponíveis')
    List<Teste> findByAtivo(Boolean ativo);
}
