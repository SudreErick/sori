package br.com.fiap.appSori.repository;

import br.com.fiap.appSori.domain.Resultado;
import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.enums.TipoTeste;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultadoRepository extends MongoRepository<Resultado, String> {
    // Busca todos os resultados de um usuário
    List<Resultado> findByUsuario(Usuario usuario);

    // Busca resultados de um usuário por tipo de teste
    List<Resultado> findByUsuarioAndTeste_Tipo(Usuario usuario, TipoTeste tipo);
}
