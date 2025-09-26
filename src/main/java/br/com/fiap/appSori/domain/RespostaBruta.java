package br.com.fiap.appSori.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaBruta {
    private String perguntaId;
    private int valorAtribuido; // Valor da resposta (ex: 0, 1, 2, 3)
}
