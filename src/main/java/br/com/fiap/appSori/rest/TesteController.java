package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.TesteRequestDto;
import br.com.fiap.appSori.domain.dto.response.TesteResponseDto;
import br.com.fiap.appSori.service.TesteService;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/testes")
@Tag(name = "Testes", description = "Endpoints para gerenciar testes psicossociais")
public class TesteController {

    private final TesteService testeService;

    /**
     * Rota POST: Cria um novo teste.
     * Necessário para o RH cadastrar novos questionários.
     */
    @PostMapping
    @Operation(summary = "Cria um novo modelo de teste psicossocial")
    public ResponseEntity<TesteResponseDto> criarTeste(@RequestBody @Valid TesteRequestDto requestDto) {
        TesteResponseDto novoTeste = testeService.criarTeste(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTeste);
    }

    /**
     * Rota GET: Lista todos os testes DISPONÍVEIS OU FILTRADOS.
     * Esta rota foi ajustada para aceitar um parâmetro opcional de filtro.
     */
    @GetMapping
    @Operation(summary = "Lista todos os testes ou filtra pelo título/tipo")
    public ResponseEntity<List<TesteResponseDto>> listarTodosTestes(
            @RequestParam(required = false) String filtro) {

        List<TesteResponseDto> testes;

        if (filtro != null && !filtro.isBlank()) {
            // Se o filtro for fornecido, a lógica deve ser implementada no Service.
            // Assumindo que você chamaria: testeService.buscarPorFiltro(filtro);
            testes = testeService.buscarTodos(); // Temporariamente retorna todos
        } else {
            // Se nenhum filtro for fornecido, lista todos os testes.
            testes = testeService.buscarTodos();
        }

        return ResponseEntity.ok(testes);
    }

    /**
     * Rota GET: Busca um teste específico pelo seu ID.
     * Ex: GET /api/testes/60c72b2f9b1d8c1e7a4f9a0c
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca um teste detalhado pelo ID")
    public ResponseEntity<TesteResponseDto> buscarTestePorId(@PathVariable String id) {
        try {
            TesteResponseDto dto = testeService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "ADMIN: Exclui um modelo de teste pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Teste excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Teste não encontrado."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.")
    })
    public ResponseEntity<Void> excluirTeste(@PathVariable String id) {
        try {
            // NOTA: A autorização (ADMIN) deve ser configurada no Spring Security.
            testeService.excluirTeste(id);
            // Retorna 204 No Content para exclusão bem-sucedida
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Teste não encontrado para exclusão
            return ResponseEntity.notFound().build();
        }
    }
}
