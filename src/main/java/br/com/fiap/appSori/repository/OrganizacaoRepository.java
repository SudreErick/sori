package br.com.fiap.appSori.repository;

import br.com.fiap.appSori.domain.Organizacao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganizacaoRepository extends MongoRepository<Organizacao, String> {
}
