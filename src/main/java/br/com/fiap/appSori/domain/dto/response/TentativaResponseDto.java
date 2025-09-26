package br.com.fiap.appSori.domain.dto.response;

import br.com.fiap.appSori.domain.dto.RespostaBrutaDto;
import br.com.fiap.appSori.domain.enums.StatusTentativa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TentativaResponseDto {
    private String id;

    // Informações básicas do Teste (para o usuário saber qual teste é)
    private String testeId;
    private String nomeTeste;

    private StatusTentativa status;
    private int totalRespostasSalvas;

    // Lista das respostas salvas até o momento
    private List<RespostaBrutaDto> respostas;

    private ZonedDateTime dataInicio;
    private ZonedDateTime dataAtualizacao;
}
