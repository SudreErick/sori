package br.com.fiap.appSori.rest;

import br.com.fiap.appSori.domain.dto.request.CheckInDiarioRequestDto;
import br.com.fiap.appSori.domain.dto.response.CheckInDiarioResponseDto;
import br.com.fiap.appSori.domain.dto.response.EstatisticaSentimentoResponseDto;
import br.com.fiap.appSori.service.CheckInDiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkins")
@Tag(name = "Check-ins Diários", description = "Endpoints para registro de humor e estatísticas de sentimentos")
@RequiredArgsConstructor
public class CheckInDiarioController {
    private final CheckInDiarioService checkinDiarioService;
    /**
     * Rota POST: /api/checkins
     * Usada para registrar o check-in diário do usuário logado.
     * Retorna 201 Created.
     */
    @PostMapping
    @Operation(summary = "Registra o check-in de sentimentos do dia")
    public ResponseEntity<CheckInDiarioResponseDto> registrarCheckin(
            @RequestBody @Valid CheckInDiarioRequestDto requestDto) {

        // O Service implementa a regra de "um check-in por dia"
        CheckInDiarioResponseDto novoCheckin = checkinDiarioService.registrarCheckin(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCheckin);
    }

    /**
     * Rota GET: /api/checkins/estatisticas?dias={dias}
     * Usada pelo Dashboard para mostrar o "Sentimento do Período".
     * O parâmetro 'dias' controla o período (ex: 7, 30, 365).
     */
    @GetMapping("/estatisticas")
    @Operation(summary = "Calcula estatísticas de sentimentos (frequência e porcentagem) para o dashboard")
    public ResponseEntity<EstatisticaSentimentoResponseDto> obterEstatisticas(
            @RequestParam(defaultValue = "7") int dias) { // Padrão é semanal (7 dias)

        // O Service faz a busca, contagem, e cálculo de porcentagem
        EstatisticaSentimentoResponseDto estatisticas = checkinDiarioService.calcularEstatisticasSentimento(dias);
        return ResponseEntity.ok(estatisticas);
    }
}
