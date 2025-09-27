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
     * Rotas listadas aqui são consideradas públicas, ignorando a validação JWT.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 1. Rotas de Autenticação (Cadastro e Login)
        if (path.startsWith("/api/auth/")) {
            return true;
        }

        // 2. Rotas do Swagger/OpenAPI
        if (path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")) {
            return true;
        }

        // 3. Rota GET /api/testes (Se ela foi liberada no SecurityConfig)
        if (request.getMethod().equals("GET") && path.equals("/api/testes")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Esta lógica SÓ será executada se shouldNotFilter retornar FALSE.

        String token = this.recoverToken(request);

        if (token != null) {
            String email = tokenService.getSubject(token);
            if (email != null) {
                var userDetails = authenticationService.loadUserByUsername(email);
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Se o token for nulo nesta fase, o Spring Security cuidará de gerar o 403/401
        // por causa do .anyRequest().authenticated()
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
