package br.com.fiap.appSori.service.auth;

import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.enums.Role;
import br.com.fiap.appSori.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthenticationService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra um novo usuário com criptografia de senha e define o perfil padrão.
     */
    public Usuario registrar(Usuario usuario) {
        // ⭐️ CORREÇÃO APLICADA: Define o perfil padrão como CLIENTE
        if (usuario.getRole() == null) {
            usuario.setRole(Role.CLIENTE);
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // O repositório retorna o objeto Usuario, que implementa UserDetails
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

}
