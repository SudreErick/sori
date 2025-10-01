package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.CheckInDiarioRequestDto;
import br.com.fiap.appSori.domain.dto.response.CheckInDiarioResponseDto;
import br.com.fiap.appSori.domain.dto.response.EstatisticaSentimentoResponseDto;
import br.com.fiap.appSori.service.CheckInDiarioService;
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
@RequestMapping("/api/checkins")
@Tag(name = "Check-ins Diários", description = "Endpoints para registro de humor e estatísticas de sentimentos")
@RequiredArgsConstructor
public class CheckInDiarioController {
    private final CheckInDiarioService checkinDiarioService;

    // --- ROTAS DE CLIENTE ---

    /**
     * Rota POST: /api/checkins
     * Usada para registrar o check-in diário do usuário logado.
     */
    @PostMapping
    @Operation(summary = "Registra o check-in de sentimentos do dia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Check-in registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Check-in para hoje já foi registrado."),
            @ApiResponse(responseCode = "401", description = "Não autorizado.")
    })
    public ResponseEntity<CheckInDiarioResponseDto> registrarCheckin(
            @RequestBody @Valid CheckInDiarioRequestDto requestDto) {

        // O Service implementa a regra de "um check-in por dia"
        CheckInDiarioResponseDto novoCheckin = checkinDiarioService.registrarCheckin(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCheckin);
    }

    /**
     * Rota GET: /api/checkins/estatisticas?dias={dias}
     * Estatísticas de check-ins do USUÁRIO LOGADO.
     */
    @GetMapping("/estatisticas")
    @Operation(summary = "Calcula estatísticas de sentimentos (frequência e porcentagem) para o dashboard do usuário logado")
    public ResponseEntity<EstatisticaSentimentoResponseDto> obterEstatisticas(
            @RequestParam(defaultValue = "7") int dias) {

        EstatisticaSentimentoResponseDto estatisticas = checkinDiarioService.calcularEstatisticasSentimento(dias);
        return ResponseEntity.ok(estatisticas);
    }

    // --- NOVAS ROTAS DE ADMIN (ACESSOS GLOBAIS) ---

    /**
     * Rota GET: /api/checkins/global
     * ADMIN: Retorna a lista de todos os check-ins de TODOS os usuários.
     * Protegido por hasRole('ADMIN').
     */
    @GetMapping("/global")
    @Operation(summary = "ADMIN: Lista todos os check-ins de todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de check-ins globais retornada."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.")
    })
    public ResponseEntity<List<CheckInDiarioResponseDto>> listarTodosCheckinsGlobais() {
        List<CheckInDiarioResponseDto> checkins = checkinDiarioService.buscarTodosCheckinsGlobais();
        return ResponseEntity.ok(checkins);
    }

    /**
     * Rota GET: /api/checkins/estatisticas/global?dias={dias}
     * ADMIN: Calcula estatísticas de sentimentos em cima de TODOS os usuários.
     * Protegido por hasRole('ADMIN').
     */
    @GetMapping("/estatisticas/global")
    @Operation(summary = "ADMIN: Calcula estatísticas de sentimentos (frequência e porcentagem) de todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas globais retornadas."),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Requer perfil ADMIN.")
    })
    public ResponseEntity<EstatisticaSentimentoResponseDto> calcularEstatisticasGlobais(
            @RequestParam(defaultValue = "30") int dias) { // Padrão mais longo para visão gerencial

        EstatisticaSentimentoResponseDto estatisticas =
                checkinDiarioService.calcularEstatisticasSentimentoGlobal(dias);

        return ResponseEntity.ok(estatisticas);
    }
}
