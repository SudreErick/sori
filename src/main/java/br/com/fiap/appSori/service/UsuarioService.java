package br.com.fiap.appSori.service;

import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.enums.Role;
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
        // NOTA: A definição da Role padrão (CLIENTE) deve ser feita no AuthenticationService
        // ou no construtor do domínio Usuario, mas não precisa ser repetida aqui se já estiver lá.
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

    // ⭐️ NOVO MÉTODO: Gerenciamento de Perfil por Administrador
    /**
     * ADMIN: Atualiza o perfil (Role) de um usuário específico.
     * @param usuarioId O ID do usuário a ser modificado.
     * @param novoPerfil O novo perfil a ser atribuído (ex: ADMIN, GESTOR_ORG).
     * @return O objeto Usuario atualizado.
     */
    public Usuario atualizarPerfilUsuario(String usuarioId, Role novoPerfil) {
        // 1. Busca o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // 2. Atualiza o perfil
        usuario.setRole(novoPerfil);
        usuario.setAtualizadoEm(ZonedDateTime.now());

        // 3. Salva e retorna
        return usuarioRepository.save(usuario);
    }
}
