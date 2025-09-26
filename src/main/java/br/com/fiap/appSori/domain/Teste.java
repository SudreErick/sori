package br.com.fiap.appSori.domain;

import br.com.fiap.appSori.domain.enums.TipoTeste;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor
@Document(collection = "testes")
public class Teste {

    @Id
    private String id;
    private TipoTeste tipo;
    private String titulo;
    private String descricao;
    private int tempoEstimadoMinutos;
    private List<Pergunta> perguntas;
}


