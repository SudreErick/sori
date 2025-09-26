package br.com.fiap.appSori.domain.dto.request;

import br.com.fiap.appSori.domain.dto.RespostaBrutaDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TentativaRequestDto {
    // ID do Teste. É obrigatório ao INICIAR uma nova tentativa.
    private String testeId;

    // Lista de todas as respostas dadas até o momento.
    // É obrigatório ao ATUALIZAR ou CONCLUIR.
    @NotEmpty(message = "A lista de respostas não pode ser vazia para atualização ou conclusão.")
    private List<RespostaBrutaDto> respostas;

    // Sinaliza se esta requisição deve CONCLUIR o teste.
    // O valor padrão, se omitido, pode ser 'false' (apenas salvar o progresso).
    private boolean concluir = false;
}
