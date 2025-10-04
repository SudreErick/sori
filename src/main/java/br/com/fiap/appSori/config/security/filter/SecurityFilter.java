// main\java\br\com\fiap\appSori\config\security\filter\SecurityFilter.java
package br.com.fiap.appSori.config.security.filter;

import br.com.fiap.appSori.service.auth.AuthenticationService;
import br.com.fiap.appSori.service.auth.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final AuthenticationService authenticationService;

    @Autowired
    public SecurityFilter(TokenService tokenService, AuthenticationService authenticationService) {
        this.tokenService = tokenService;
        this.authenticationService = authenticationService;
    }

    /**
     * Define quais requisições NÃO DEVEM passar por este filtro.
     * Deixamos apenas as rotas absolutamente públicas aqui.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Rotas de Autenticação (ex: /api/auth/login) são públicas e não precisam de validação de token.
        if (path.startsWith("/api/auth")) {
            return true;
        }

        // Rotas do Swagger/OpenAPI também são públicas.
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs")) {
            return true;
        }

        // A rota de registro de usuário (POST /api/usuarios) também é pública.
        if (path.equals("/api/usuarios") && request.getMethod().equals("POST")) {
            return true;
        }

        // Todas as outras rotas DEVEM passar pelo filtro para que o token seja validado.
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Passo 1: Tenta recuperar o token do header "Authorization".
        String token = this.recoverToken(request);

        // Passo 2: Se um token foi encontrado, continua com a validação.
        if (token != null) {
            // Valida o token e extrai o "subject" (que é o email do usuário).
            String email = tokenService.getSubject(token);

            if (email != null) {
                // Com o email, busca os dados completos do usuário no banco de dados.
                // Esta é a etapa CRUCIAL: 'userDetails' conterá as permissões (roles) ATUAIS do usuário no banco.
                var userDetails = authenticationService.loadUserByUsername(email);

                // Cria o objeto de autenticação que o Spring Security usará.
                // O terceiro argumento, 'userDetails.getAuthorities()', é o mais importante.
                // Ele informa ao Spring quais são as permissões do usuário (ex: [ROLE_ADMIN]).
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Define o usuário como autenticado para esta requisição,
                // disponibilizando suas permissões para o SecurityConfig.
                SecurityContextHolder.getContext().setAuthentication(authentication);

                /*
                 * [DICA DE DEBUG]
                 * Se ainda tiver dúvidas, descomente a linha abaixo para ver no console
                 * exatamente quais permissões estão sendo carregadas para cada requisição.
                 */
                // System.out.println(">>> Rota: " + request.getRequestURI() + " | Usuário: " + userDetails.getUsername() + " | Permissões: " + userDetails.getAuthorities());
            }
        }

        // Continua a execução da cadeia de filtros.
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar para extrair o token JWT do header "Authorization".
     * Ele remove o prefixo "Bearer ".
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7); // Remove "Bearer "
    }
}