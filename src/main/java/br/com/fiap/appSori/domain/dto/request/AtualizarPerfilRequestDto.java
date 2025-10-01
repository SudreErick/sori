package br.com.fiap.appSori.domain.dto.request;

import br.com.fiap.appSori.domain.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarPerfilRequestDto {
    @NotNull(message = "O perfil é obrigatório.")
    private Role perfil;
}
