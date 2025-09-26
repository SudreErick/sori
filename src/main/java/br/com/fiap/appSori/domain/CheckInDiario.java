package br.com.fiap.appSori.domain;

import br.com.fiap.appSori.domain.enums.SentimentoDiario;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "checkins_diarios")
public class CheckInDiario {
    @Id
    private String id;

    // Referência ao usuário que fez o check-in
    @DBRef
    private Usuario usuario;

    // Data específica do check-in (garante um check-in por dia por usuário)
    private LocalDate dataCheckin = LocalDate.now();

    // Lista de sentimentos escolhidos (usando o Enum para padronização)
    private List<SentimentoDiario> sentimentos;

    // Texto livre para anotações rápidas
    private String anotacao;

    // Timestamp de criação
    private ZonedDateTime dataRegistro = ZonedDateTime.now();
}
