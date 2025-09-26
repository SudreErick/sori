package br.com.fiap.appSori.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Document(collection = "relacionamentos")
public class Relacionamento {
    @Id
    private String id;
    private String usuarioId;
    private String organizacaoId;
    private String cargo;
    private ZonedDateTime criadoEm;
    private ZonedDateTime atualizadoEm;
    private boolean ativo;

    public Relacionamento() {
        this.id = UUID.randomUUID().toString();
        this.ativo = true;
        this.criadoEm = ZonedDateTime.now();
        this.atualizadoEm = ZonedDateTime.now();
    }
}
