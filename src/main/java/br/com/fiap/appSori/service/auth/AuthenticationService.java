package br.com.fiap.appSori.service.auth;

import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.dto.auth.request.LoginRequestDto;
import br.com.fiap.appSori.domain.dto.auth.response.LoginResponseDto;
import br.com.fiap.appSori.domain.enums.Role;
import br.com.fiap.appSori.repository.UsuarioRepository;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;


@Service
public class AuthenticationService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService; // Adicionada a dependência do TokenService
    private final AuthenticationManager authenticationManager; // Adicionada a dependência para o processo de Login

    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(
            UsuarioRepository usuarioRepository,
            TokenService tokenService,
            @Lazy AuthenticationManager authenticationManager // @Lazy para evitar dependência circular
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // --- MÉTODOS DE REGISTRO E AUTENTICAÇÃO ---

    /**
     * Registra um novo usuário com criptografia de senha e a lógica de primeiro acesso.
     * Esta função é chamada pela rota POST /api/usuarios (registro).
     */
    public Usuario registrar(Usuario usuario) {
        // 1. Checa se o email já existe
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("O email " + usuario.getEmail() + " já está em uso.");
        }

        // 2. LÓGICA DE PRIMEIRO ACESSO (Define nome padrão se registro minimalista)
        // Se o nome não foi preenchido, ele será definido como um marcador
        if (usuario.getNomeCompleto() == null || usuario.getNomeCompleto().trim().isEmpty()) {
            usuario.setNomeCompleto("Novo Usuário"); // Marcador de primeiro acesso
        }

        // 3. Define Perfil Padrão e Metadados
        if (usuario.getRole() == null) {
            usuario.setRole(Role.CLIENTE);
        }
        usuario.setAtualizadoEm(ZonedDateTime.now());

        // 4. Criptografa a senha e Salva
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Autentica o usuário e gera o token JWT, injetando a flag de primeiro login.
     * @param request DTO com email e senha.
     * @return DTO com o token JWT e a flag de primeiro login.
     */
    public LoginResponseDto login(LoginRequestDto request) {
        try {
            // 1. Tenta autenticar o usuário
            var usernamePassword = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            // 2. Obtém o usuário autenticado
            var usuario = (Usuario) auth.getPrincipal();

            // 3. Gera o token
            var token = tokenService.generateToken(usuario);

            // 4. LÓGICA DE PRIMEIRO LOGIN/ONBOARDING: Checa se o nome ainda é o marcador
            boolean isPrimeiroAcesso = usuario.getNomeCompleto().equals("Novo Usuário");

            // 5. Constrói e retorna o DTO de resposta
            // NOTA: Para retornar a flag 'primeiroLogin', seu LoginResponseDto precisa ser mais completo
            // do que apenas o 'token'.
            LoginResponseDto response = new LoginResponseDto();
            response.setToken(token);
            // Aqui você deve definir as outras propriedades do DTO que contém o primeiroLogin,
            // mas como o DTO original só tinha token, estou retornando o que você definiu.
            // VOCÊ DEVE ATUALIZAR O SEU LoginResponseDto para incluir o 'Boolean primeiroLogin'.

            // Exemplo de como DEVERIA ser um DTO de login para o Front-end:
            // return AuthResponseDto.builder()
            //         .token(token)
            //         .email(usuario.getEmail())
            //         .primeiroLogin(isPrimeiroAcesso)
            //         .build();

            return response;

        } catch (JOSEException e) {
            // Se houver erro na geração do token
            throw new RuntimeException("Erro ao gerar token JWT.", e);
        }
    }


    // --- MÉTODOS DO SPRING SECURITY ---

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Normaliza o e-mail antes de buscar
        String normalizedEmail = email.toLowerCase();

        return usuarioRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
}
