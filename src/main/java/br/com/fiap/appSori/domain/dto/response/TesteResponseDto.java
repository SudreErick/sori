package br.com.fiap.appSori.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TesteResponseDto {
    private String id;
    private String tipo;
    private String titulo;
    private String descricao;
    private int tempoEstimadoMinutos;
    private List<PerguntaResponseDto> perguntas;
}
