package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.TentativaRequestDto;
import br.com.fiap.appSori.domain.dto.response.TentativaResponseDto;
import br.com.fiap.appSori.service.TentativaService;
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

@RestController
@RequestMapping("/api/tentativas")
@Tag(name = "Tentativas", description = "Endpoints para gerenciar o progresso dos testes em andamento")
@RequiredArgsConstructor
public class TentativaController {

    private final TentativaService tentativaService;

    // --- ROTAS DE CLIENTE ---

    /**
     * Rota POST: Inicia um novo teste, salva o progresso ou conclui uma tentativa.
     * Esta é a rota principal, que cobre o ciclo de vida completo.
     */
    @PostMapping
    @Operation(summary = "Inicia um teste, salva o progresso ou conclui uma tentativa")
    public ResponseEntity<TentativaResponseDto> iniciarOuSalvarProgresso(
            @RequestBody @Valid TentativaRequestDto requestDto) {
        try {
            TentativaResponseDto response = tentativaService.iniciarOuAtualizarTentativa(requestDto);

            // Retorna 201 Created se for a primeira vez que salvou, ou 200 OK se for atualização/conclusão
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            // Erros de negócio (ex: Usuário não encontrado, tentativa já ativa)
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Rota GET: Lista todas as tentativas (histórico completo) do usuário logado.
     */
    @GetMapping
    @Operation(summary = "Lista todas as tentativas (histórico completo) do usuário logado")
    public ResponseEntity<List<TentativaResponseDto>> listarTodasTentativas() {
        // Assume-se que o serviço busca as tentativas pelo usuário no contexto de segurança.
        List<TentativaResponseDto> tentativas = tentativaService.buscarTodasTentativasDoUsuario();
        return ResponseEntity.ok(tentativas);
    }

    /**
     * Rota GET: Busca uma tentativa específica pelo seu ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca uma tentativa específica pelo ID")
    public ResponseEntity<TentativaResponseDto> buscarTentativaPorId(@PathVariable String id) {
        try {
            TentativaResponseDto response = tentativaService.buscarTentativaPorId(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Tentativa não encontrada ou não pertence ao usuário
            return ResponseEntity.notFound().build();
        }
    }

    // ⭐️ NOVA ROTA DE ADMIN (ACESSOS GLOBAIS)
    /**
     * Rota GET: /api/tentativas/global
     * ADMIN: Retorna a lista de todas as tentativas de TODOS os usuários.
     * Protegido por hasRole('ADMIN') no SecurityConfig.
     * @return Lista de todas as Tentativas de todos os usuários.
     */
    @GetMapping("/global")
    @Operation(summary = "ADMIN: Lista todas as tentativas (histórico completo) de todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tentativas globais retornada."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.")
    })
    public ResponseEntity<List<TentativaResponseDto>> listarTodasTentativasGlobais() {
        List<TentativaResponseDto> tentativas = tentativaService.buscarTodasTentativasGlobais();
        return ResponseEntity.ok(tentativas);
    }
}
