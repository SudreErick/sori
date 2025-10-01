package br.com.fiap.appSori.domain.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    /**
     * Usuário padrão, só pode acessar seus próprios dados e fazer check-ins.
     */
    CLIENTE,

    /**
     * Usuário administrador, tem acesso a todos os dados e rotas de gestão.
     */
    ADMIN,

    /**
     * Usuário gestor de uma organização, pode acessar dados de sua organização.
     */
    GESTOR_ORG;

    /**
     * O Spring Security espera que o nome da autoridade comece com "ROLE_".
     */
    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
