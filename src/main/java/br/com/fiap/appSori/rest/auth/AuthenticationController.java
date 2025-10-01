package br.com.fiap.appSori.rest.auth;

import br.com.fiap.appSori.domain.Usuario;
import br.com.fiap.appSori.domain.dto.auth.request.LoginRequestDto;
import br.com.fiap.appSori.domain.dto.request.UsuarioRequestDto;
import br.com.fiap.appSori.domain.dto.auth.response.LoginResponseDto;
import br.com.fiap.appSori.domain.dto.response.UsuarioResponseDto;

import br.com.fiap.appSori.mapper.UsuarioMapper;
import br.com.fiap.appSori.service.auth.AuthenticationService;
import br.com.fiap.appSori.service.auth.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final UsuarioMapper usuarioMapper;

    public AuthenticationController(AuthenticationManager authenticationManager, AuthenticationService authenticationService, TokenService tokenService, UsuarioMapper usuarioMapper) {
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.")
    })
    public ResponseEntity<UsuarioResponseDto> register(@RequestBody @Valid UsuarioRequestDto requestDTO) {
        var usuario = usuarioMapper.toDomain(requestDTO);
        var usuarioSalvo = authenticationService.registrar(usuario);
        return ResponseEntity.ok(usuarioMapper.toDto(usuarioSalvo));
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
            var authentication = new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getSenha());
            var authenticated = authenticationManager.authenticate(authentication);

            //CORREÇÃO APLICADA AQUI: Cast de Object para Usuario
            // Assumimos que o objeto principal é de fato uma instância de Usuario.
            Usuario usuario = (Usuario) authenticated.getPrincipal();

            // Passa o objeto Usuario para o TokenService, resolvendo o erro de tipo.
            var token = tokenService.generateToken(usuario);

            return ResponseEntity.ok(new LoginResponseDto(token));
        } catch (BadCredentialsException e) {
            System.err.println("Erro de credenciais inválidas: " + e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (AuthenticationException e) {
            System.err.println("Erro de autenticação: " + e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            System.err.println("Erro inesperado durante o login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
