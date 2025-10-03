package br.com.fiap.appSori.domain;

import br.com.fiap.appSori.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    private String cargo;
    private ZonedDateTime criadoEm;
    private ZonedDateTime atualizadoEm;
    private boolean ativo;

    //ADIÇÃO 1: Campo de Perfil (Role)
    private Role role;

    // O Lombok gera os Getters e Setters para o campo 'role'

    public Usuario() {
        this.id = UUID.randomUUID().toString();
        this.ativo = true;
        this.criadoEm = ZonedDateTime.now();
        this.atualizadoEm = ZonedDateTime.now();
        // ADIÇÃO 2: Define CLIENTE como padrão
        this.role = Role.CLIENTE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // MODIFICAÇÃO: Retorna o perfil (role) do usuário
        return List.of(this.role);
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }
}
