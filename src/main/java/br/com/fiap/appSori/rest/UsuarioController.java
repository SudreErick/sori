package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.AtualizarPerfilRequestDto;
import br.com.fiap.appSori.domain.dto.request.UsuarioRequestDto;
import br.com.fiap.appSori.domain.dto.response.UsuarioResponseDto;
import br.com.fiap.appSori.mapper.UsuarioMapper;
import br.com.fiap.appSori.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciar os usuários")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @Operation(summary = "Cria um novo usuário", description = "Endpoint para registrar um novo usuário na plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida.")
    })
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@Valid @RequestBody UsuarioRequestDto requestDTO) {
        var usuarioDomain = usuarioMapper.toDomain(requestDTO);
        var usuarioSalvo = usuarioService.criarUsuario(usuarioDomain);
        var responseDTO = usuarioMapper.toDto(usuarioSalvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso.")
    })
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios() {
        var usuarios = usuarioService.buscarTodos();
        var responseDTOs = usuarios.stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário por ID", description = "Retorna um usuário específico com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(@PathVariable String id) {
        return usuarioService.buscarPorId(id)
                .map(usuarioMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    @Operation(summary = "Busca um usuário por e-mail", description = "Retorna um usuário com base no seu endereço de e-mail.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorEmail(@RequestParam String email) {
        return usuarioService.buscarPorEmail(email)
                .map(usuarioMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf")
    @Operation(summary = "Busca um usuário por CPF", description = "Retorna um usuário com base no seu CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorCpf(@RequestParam String cpf) {
        return usuarioService.buscarPorCpf(cpf)
                .map(usuarioMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ativos")
    @Operation(summary = "Lista todos os usuários ativos", description = "Retorna uma lista de todos os usuários que estão ativos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários ativos retornada com sucesso.")
    })
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuariosAtivos() {
        var usuariosAtivos = usuarioService.buscarAtivos();
        var responseDTOs = usuariosAtivos.stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // ⭐️ NOVO ENDPOINT DE ADMIN: Atualizar Perfil (Role)
    /**
     * Rota PUT: /api/usuarios/{id}/perfil
     * ADMIN: Permite que um administrador altere o perfil (Role) de outro usuário.
     * Esta rota deve ser protegida por hasRole('ADMIN').
     */
    @PutMapping("/{id}/perfil")
    @Operation(summary = "ADMIN: Atualiza o perfil (role) de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<UsuarioResponseDto> atualizarPerfil(
            @PathVariable String id,
            @RequestBody @Valid AtualizarPerfilRequestDto requestDto) {
        try {
            var usuarioAtualizado = usuarioService.atualizarPerfilUsuario(id, requestDto.getPerfil());
            return ResponseEntity.ok(usuarioMapper.toDto(usuarioAtualizado));
        } catch (RuntimeException e) {
            // Captura o erro 'Usuário não encontrado' lançado pelo service
            return ResponseEntity.notFound().build();
        }
    }

}
