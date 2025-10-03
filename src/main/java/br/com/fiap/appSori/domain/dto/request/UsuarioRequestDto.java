package br.com.fiap.appSori.domain.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDto {
    // 1. Campos obrigatórios para o registro MINIMALISTA
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email fornecido não é válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    private String senha;
    private String nomeCompleto;
    private String cargo;

}
