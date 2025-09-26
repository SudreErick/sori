package br.com.fiap.appSori.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstatisticaSentimentoResponseDto {
    // Sentimento com a maior contagem no período
    private String sentimentoFrequente;

    // Detalhes (usado para a modal "Ver Detalhes")
    private List<DetalheSentimento> detalhes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalheSentimento {
        private String sentimento;
        private int contagem;
        private double porcentagem; // Ex: 28.0
    }
}
