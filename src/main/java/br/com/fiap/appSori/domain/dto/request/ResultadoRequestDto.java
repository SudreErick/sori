package br.com.fiap.appSori.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoRequestDto {
    private String testeId;
    private List<RespostaUsuarioRequestDto> respostas;
}
