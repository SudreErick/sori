package br.com.fiap.appSori.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoResponseDto {
    private String id;
    private String tipoTeste;
    private String testeId;
    private String usuarioId;
    private ZonedDateTime dataRealizacao;
    private int pontuacaoTotal;
    private String nivelRisco;
    private List<RespostaUsuarioResponseDto> respostas; // Usando a nova classe

}

