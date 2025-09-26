package br.com.fiap.appSori.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Classe de sub-DTO para a resposta
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaUsuarioResponseDto {
    private String perguntaId;
    private String perguntaTexto;
    private String respostaSelecionada;
    private int valorAtribuido;
}
