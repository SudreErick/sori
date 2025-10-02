package br.com.fiap.appSori.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerguntaResponseDto {
    private String id;
    private String texto;
    private List<OpcaoRespostaResponseDto> opcoes;
}
