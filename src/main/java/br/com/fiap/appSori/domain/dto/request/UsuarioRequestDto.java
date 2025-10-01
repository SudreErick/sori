package br.com.fiap.appSori.domain.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDto {
    private String nomeCompleto;
    private String email;
    private String senha;
    private String cpf;
    private String telefone;
    private String cargo;
}
