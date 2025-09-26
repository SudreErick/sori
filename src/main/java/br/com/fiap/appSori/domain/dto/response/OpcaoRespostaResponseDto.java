package br.com.fiap.appSori.domain.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcaoRespostaResponseDto {
    private String texto;
    private int valor;
}
