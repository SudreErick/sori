package br.com.fiap.appSori.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario  implements UserDetails {
    @Id
    private String id;
    private String nomeCompleto;
    private String email;
    private String senha;
    private String cpf;
    private String telefone;
    private String cargo;
    private ZonedDateTime criadoEm;
    private ZonedDateTime atualizadoEm;
    private boolean ativo;

    public Usuario() {
        this.id = UUID.randomUUID().toString();
        this.ativo = true;
        this.criadoEm = ZonedDateTime.now();
        this.atualizadoEm = ZonedDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por enquanto, não vamos usar roles ou permissões.
        // Retornamos uma lista vazia de autoridades.
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        // Retorna a senha do usuário
        return this.senha;
    }

    @Override
    public String getUsername() {
        // O Spring Security usará o e-mail como nome de usuário para autenticação
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // A conta nunca expira
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // A conta nunca é bloqueada
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // As credenciais nunca expiram
        return true;
    }

    @Override
    public boolean isEnabled() {
        // A conta está habilitada se o campo 'ativo' for verdadeiro
        return this.ativo;
    }
}
