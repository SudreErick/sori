package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.ResultadoRequestDto;
import br.com.fiap.appSori.domain.dto.response.ResultadoResponseDto;
import br.com.fiap.appSori.service.ResultadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/resultados")
@Tag(name = "Resultados", description = "Endpoints para gerenciar resultados de testes psicossociais")
public class ResultadoController {

    private final ResultadoService resultadoService;

    @PostMapping
    @Operation(summary = "Salva o resultado de um teste realizado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado salvo com sucesso."),
            @ApiResponse(responseCode = "404", description = "Teste ou usuário não encontrado.")
    })
    public ResponseEntity<ResultadoResponseDto> salvarResultado(@RequestBody ResultadoRequestDto requestDto) {
        try {
            ResultadoResponseDto responseDto = resultadoService.salvarResultado(requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Rota para buscar todos os resultados do usuário logado
    @GetMapping
    @Operation(summary = "Busca todos os resultados do usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados encontrados."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<List<ResultadoResponseDto>> buscarTodosResultados() {
        try {
            List<ResultadoResponseDto> resultados = resultadoService.buscarResultadosPorUsuario();
            return ResponseEntity.ok(resultados);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Rota para buscar resultados por tipo do usuário logado
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Busca resultados do usuário por tipo de teste")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados encontrados."),
            @ApiResponse(responseCode = "400", description = "Tipo de teste inválido."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<List<ResultadoResponseDto>> buscarResultadosPorTipo(@PathVariable String tipo) {
        try {
            List<ResultadoResponseDto> resultados = resultadoService.buscarResultadosPorUsuarioETipo(tipo);
            return ResponseEntity.ok(resultados);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // NOVO ENDPOINT DE ADMIN: Acesso a todos os resultados
    @GetMapping("/global")
    @Operation(summary = "ADMIN: Busca todos os resultados de todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados encontrados."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.")
    })
    public ResponseEntity<List<ResultadoResponseDto>> listarTodosResultadosGlobais() {
        // Não precisamos de bloco try-catch, pois se o serviço lançar uma exceção,
        // ela será tratada por um @ControllerAdvice ou resultará em 500.
        // O 403 é tratado pelo Spring Security ANTES de chegar aqui.
        List<ResultadoResponseDto> resultados = resultadoService.buscarTodosResultadosGlobais();
        return ResponseEntity.ok(resultados);
    }
}
