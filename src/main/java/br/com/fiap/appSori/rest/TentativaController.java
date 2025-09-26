package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.TentativaRequestDto;
import br.com.fiap.appSori.domain.dto.response.TentativaResponseDto;
import br.com.fiap.appSori.service.TentativaService;
import io.swagger.v3.oas.annotations.Operation;
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

    /**
     * Rota POST: Inicia um novo teste, salva o progresso ou conclui uma tentativa.
     * Esta é a rota principal, que cobre o ciclo de vida completo.
     * * @param requestDto Contém o ID do teste (para iniciar) e a lista de respostas.
     * @return O status atualizado da Tentativa.
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
            // Para depuração: você pode retornar a mensagem do erro se quiser: ResponseEntity.badRequest().body(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Rota GET: Lista todas as tentativas (concluídas ou em andamento) do usuário logado.
     * * @return Lista de todas as Tentativas do usuário.
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
     * Útil para o front-end carregar um teste em andamento.
     * * @param id O ID da Tentativa.
     * @return O status detalhado da Tentativa.
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
}
