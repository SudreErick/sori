package br.com.fiap.appSori.domain.dto.response;

import br.com.fiap.appSori.domain.enums.SentimentoDiario;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CheckInDiarioResponseDto {
    private String id;
    private String usuarioId; // ID do usuário que fez o check-in (para contexto)
    private LocalDate dataCheckin;

    // Lista de sentimentos registrados
    private List<SentimentoDiario> sentimentos;

    private String anotacao;
    private ZonedDateTime dataRegistro;
}
