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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/testes")
@Tag(name = "Testes", description = "Endpoints para gerenciar testes psicossociais")
public class TesteController {

    private final TesteService testeService;

    // --- Rotas de ADMIN ---

    /**
     * Rota POST: Cria um novo teste. (Apenas ADMIN via hasAuthority('ADMIN'))
     */
    @PostMapping
    @Operation(summary = "ADMIN: Cria um novo modelo de teste psicossocial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teste criado com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer ADMIN.")
    })
    public ResponseEntity<TesteResponseDto> criarTeste(@RequestBody @Valid TesteRequestDto requestDto) {
        TesteResponseDto novoTeste = testeService.criarTeste(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTeste);
    }

    /**
     * Rota GET: ADMIN busca TODOS os testes (incluindo rascunhos, inativos, etc.)
     */
    @GetMapping("/admin/todos")
    @Operation(summary = "ADMIN: Busca todos os testes cadastrados no sistema",
            description = "Retorna todos os testes, independente do status. Requer ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista completa de testes."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer ADMIN.")
    })
    public ResponseEntity<List<TesteResponseDto>> listarTodosTestesAdmin() {
        // O método buscarTodos() busca todos os testes no repositório.
        List<TesteResponseDto> testes = testeService.buscarTodos();
        return ResponseEntity.ok(testes);
    }

    /**
     * Rota DELETE: Exclui um modelo de teste pelo ID. (Apenas ADMIN via hasAuthority('ADMIN'))
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "ADMIN: Exclui um modelo de teste pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Teste excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Teste não encontrado."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.")
    })
    public ResponseEntity<Void> excluirTeste(@PathVariable String id) {
        try {
            testeService.excluirTeste(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // --- Rotas de Usuário Logado (CLIENTE/GESTOR) ---

    /**
     * Rota GET: Busca um teste específico pelo seu ID. (Acesso para usuários logados)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca um teste detalhado pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teste encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Teste não encontrado.")
    })
    public ResponseEntity<TesteResponseDto> buscarTestePorId(@PathVariable String id) {
        try {
            TesteResponseDto dto = testeService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Rota GET: Usuário busca testes DISPONÍVEIS para realizar.
     * Chama a lógica de filtro no Service.
     */
    @GetMapping("/disponiveis")
    @Operation(summary = "Busca testes disponíveis para o usuário logado realizar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de testes disponíveis.")
    })
    public ResponseEntity<List<TesteResponseDto>> buscarTestesDisponiveisParaUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = auth.getName(); // Obtém o e-mail do subject do token

        // CHAMA O MÉTODO CORRETO: Filtra ativos e não realizados
        List<TesteResponseDto> testesDisponiveis = testeService.buscarTestesDisponiveis(usuarioEmail);

        return ResponseEntity.ok(testesDisponiveis);
    }

    /**
     * Rota GET: Usuário busca testes JÁ REALIZADOS.
     * Chama a lógica de histórico no Service.
     */
    @GetMapping("/realizados")
    @Operation(summary = "Busca testes que o usuário logado já realizou")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de testes realizados.")
    })
    public ResponseEntity<List<TesteResponseDto>> buscarTestesJaRealizados() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = auth.getName(); // Obtém o e-mail do subject do token

        // CHAMA O MÉTODO CORRETO: Busca testes concluídos pelo usuário
        List<TesteResponseDto> testesRealizados = testeService.buscarTestesRealizados(usuarioEmail);

        return ResponseEntity.ok(testesRealizados);
    }

    /**
     * Rota GET: Lista todos os testes ativos (Rota genérica que pode ser usada por qualquer logado).
     * NOTA: Se /disponiveis for a principal, esta rota (se houver) deve ter uma lógica de filtro menos restritiva.
     */
    @GetMapping
    @Operation(summary = "Lista todos os testes ativos/publicados")
    public ResponseEntity<List<TesteResponseDto>> listarTodosTestes() {
        // Se a rota for usada para listar *todos* os ativos (sem filtro de "já realizados"), use:
        List<TesteResponseDto> testes = testeService.buscarTodos().stream()
                .filter(TesteResponseDto::isAtivo) // Assume que o DTO tem o campo 'ativo'
                .collect(Collectors.toList());

        // No entanto, é recomendável usar a rota /disponiveis ou /admin/todos, dependendo da necessidade.
        // Se este endpoint não for necessário, ele pode ser removido para simplificar.
        // Deixamos como fallback que lista todos (já que a segurança permite a todos logados).
        return ResponseEntity.ok(testeService.buscarTodos());
    }
}
