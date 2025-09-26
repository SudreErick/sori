package br.com.fiap.appSori.domain;


import br.com.fiap.appSori.domain.enums.StatusTentativa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tentativas")
public class Tentativa {
    @Id
    private String id;

    @DBRef // Referência ao usuário que está fazendo o teste
    private Usuario usuario;

    @DBRef // Referência ao teste que está sendo respondido
    private Teste teste;

    // Status da tentativa: INICIADA, EM_ANDAMENTO, CONCLUIDA
    private StatusTentativa status;

    // Armazena a lista de respostas brutas (ID da pergunta e valor)
    private List<RespostaBruta> respostas;

    private ZonedDateTime dataInicio;
    private ZonedDateTime dataAtualizacao;

}
