package br.com.fiap.appSori.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaBrutaDto {
    // ID da Pergunta. Usado para referenciar a pergunta que foi respondida.
    private String perguntaId;

    // Valor dado à resposta (ex: 0, 1, 2, 3)
    private int valorAtribuido;
}
