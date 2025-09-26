package br.com.fiap.appSori.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaUsuarioRequestDto {
    private String perguntaId;
    private String perguntaTexto;
    private String respostaSelecionada;
    private int valorAtribuido;
}
