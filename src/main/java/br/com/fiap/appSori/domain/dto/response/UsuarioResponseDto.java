package br.com.fiap.appSori.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data // Anotação do Lombok para gerar getters, setters, etc.
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor
public class UsuarioResponseDto {
    private String id;
    private String nomeCompleto;
    private String email;
    private String cargo;
    private String role;
    private Boolean primeiroLogin;
    private ZonedDateTime criadoEm;
    private ZonedDateTime atualizadoEm;
    private boolean ativo;
}
