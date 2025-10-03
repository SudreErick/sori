package br.com.fiap.appSori.rest.auth;

import br.com.fiap.appSori.domain.dto.auth.request.LoginRequestDto;
import br.com.fiap.appSori.domain.dto.request.UsuarioRequestDto;
import br.com.fiap.appSori.domain.dto.auth.response.LoginResponseDto;
import br.com.fiap.appSori.domain.dto.response.UsuarioResponseDto;

import br.com.fiap.appSori.mapper.UsuarioMapper;
import br.com.fiap.appSori.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "409", description = "Email já em uso.")
    })
    public ResponseEntity<UsuarioResponseDto> register(@RequestBody @Valid UsuarioRequestDto requestDTO) {
        // 1. Lógica de detecção de primeiro acesso (igual ao UsuarioController)
        boolean isPrimeiroAcesso = requestDTO.getNomeCompleto() == null || requestDTO.getNomeCompleto().trim().isEmpty();

        var usuario = usuarioMapper.toDomain(requestDTO);

        // 2. Chama o service que registra (salva e criptografa a senha)
        var usuarioSalvo = authenticationService.registrar(usuario);

        // 3. Mapeia e injeta a flag de primeiro acesso na resposta
        var responseDTO = usuarioMapper.toDto(usuarioSalvo);
        responseDTO.setPrimeiroLogin(isPrimeiroAcesso);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário e retorna um token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido."),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDTO) {
        try {
            // Chamamos o service, que agora encapsula a autenticação (AuthenticationManager),
            // a geração do token (TokenService) e a lógica de 'primeiroLogin'.
            LoginResponseDto response = authenticationService.login(requestDTO);

            // Se o login for bem-sucedido, o service retorna o DTO completo.
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // Captura falhas de autenticação (BadCredentialsException)
            System.err.println("Erro de autenticação (email ou senha inválidos): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Retorna 401
        } catch (Exception e) {
            // Captura erros inesperados (como falha na geração do token)
            System.err.println("Erro inesperado durante o login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500
        }
    }
}
