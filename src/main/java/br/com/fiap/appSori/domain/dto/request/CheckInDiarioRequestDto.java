package br.com.fiap.appSori.domain.dto.request;

import br.com.fiap.appSori.domain.enums.SentimentoDiario;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CheckInDiarioRequestDto {
    // Lista de sentimentos escolhidos (usando o Enum para padronização)
    @NotEmpty(message = "Pelo menos um sentimento deve ser selecionado.")
    private List<SentimentoDiario> sentimentos;

    // Texto livre para anotações rápidas (opcional)
    private String anotacao;
}
