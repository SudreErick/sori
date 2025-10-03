package br.com.fiap.appSori.repository;

import br.com.fiap.appSori.domain.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface UsuarioRepository extends MongoRepository<Usuario, String>{

    // Método para buscar um usuário pelo email
    Optional<Usuario> findByEmail(String email);

    // Método para buscar todos os usuários que estão ativos
    List<Usuario> findByAtivoTrue();
}
