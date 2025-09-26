package br.com.fiap.appSori.domain.dto.request;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerguntaRequestDto {
    private String texto;
    private List<OpcaoRespostaRequestDto> opcoes;
}
