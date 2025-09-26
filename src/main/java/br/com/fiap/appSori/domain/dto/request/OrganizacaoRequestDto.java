package br.com.fiap.appSori.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Anotação do Lombok para gerar getters, setters, etc.
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor
public class OrganizacaoRequestDto {
    private String nome;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String emailContato;
}
