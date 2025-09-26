package br.com.fiap.appSori.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelacionamentoResponseDto {
    private String id;
    private String usuarioId;
    private String organizacaoId;
    private String cargo;
    private ZonedDateTime criadoEm;
    private ZonedDateTime atualizadoEm;
    private boolean ativo;
}
