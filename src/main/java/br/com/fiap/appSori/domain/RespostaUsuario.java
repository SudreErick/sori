package br.com.fiap.appSori.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaUsuario {
    private String perguntaTexto;
    private String respostaSelecionada; // O texto da opção
    private int valorAtribuido; // A pontuação da opção
}
