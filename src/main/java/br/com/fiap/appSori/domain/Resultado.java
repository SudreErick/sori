package br.com.fiap.appSori.domain;

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
@Document(collection = "resultados")
public class Resultado {
    @Id
    private String id;

    @DBRef // Referência para a entidade Teste
    private Teste teste;

    @DBRef // Referência para o usuário que fez o teste
    private Usuario usuario;

    private ZonedDateTime dataRealizacao;

    private int pontuacaoTotal;

    private String nivelRisco; // Ex: Baixo, Moderado, Alto

    // Subdocumento para armazenar as respostas
    private List<RespostaUsuario> respostas;
}

