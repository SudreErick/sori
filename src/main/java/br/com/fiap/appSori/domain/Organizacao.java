package br.com.fiap.appSori.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // Gera um construtor com todos os argumentos
@Document(collection = "organizacoes") // Mapeia esta classe para a coleção 'Organizacao' no MongoDB
public class Organizacao {
    @Id // Marca este campo como a chave primária (_id) do documento no MongoDB
    private String id;
    private String nome;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String emailContato;
    private boolean ativo;
    private ZonedDateTime criadoEm;
    private ZonedDateTime atualizadoEm;
}
