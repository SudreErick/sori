package br.com.fiap.appSori.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data // Anotação do Lombok para gerar getters, setters, etc.
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor
public class OrganizacaoResponseDto {
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
