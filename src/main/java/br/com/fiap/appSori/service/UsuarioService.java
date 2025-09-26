package br.com.fiap.appSori.service;

import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public Usuario criarUsuario(Usuario usuario) {
        // Normaliza o e-mail para minúsculas antes de salvar
        usuario.setEmail(usuario.getEmail().toLowerCase());
        usuario.setCriadoEm(ZonedDateTime.now());
        usuario.setAtualizadoEm(ZonedDateTime.now());
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        // Normaliza o e-mail para minúsculas antes de buscar
        return usuarioRepository.findByEmail(email.toLowerCase());
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

    public List<Usuario> buscarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }
}
