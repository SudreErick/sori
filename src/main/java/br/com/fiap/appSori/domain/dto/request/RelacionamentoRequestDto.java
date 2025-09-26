package br.com.fiap.appSori.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelacionamentoRequestDto {
    private String usuarioId;
    private String organizacaoId;
    private String cargo;
}
