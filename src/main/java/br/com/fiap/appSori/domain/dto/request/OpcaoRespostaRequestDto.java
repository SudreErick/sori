package br.com.fiap.appSori.domain.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcaoRespostaRequestDto {
    private String texto;
    private int valor;
}
