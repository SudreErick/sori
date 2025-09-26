package br.com.fiap.appSori.domain.dto.request;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TesteRequestDto {
    private String tipo;
    private String titulo;
    private String descricao;
    private int tempoEstimadoMinutos;
    private List<PerguntaRequestDto> perguntas;
}
