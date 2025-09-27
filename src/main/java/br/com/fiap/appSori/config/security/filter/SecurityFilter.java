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
     * Rotas listadas aqui são consideradas públicas (permitAll).
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // **Aprimoramento 1: Listagem explícita de rotas públicas.**

        // Rotas de Autenticação (POST /api/auth/register e /api/auth/login)
        if (path.startsWith("/api/auth/") || path.equals("/api/auth")) {
            return true;
        }

        // Rotas do Swagger/OpenAPI
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs")) {
            return true;
        }

        // Rota GET /api/testes (Se ela for liberada)
        if (method.equals("GET") && path.startsWith("/api/testes")) {
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

        // Continua a cadeia de filtros.
        // Se a autenticação falhar, o Spring Security, devido ao .anyRequest().authenticated(),
        // emitirá o 401 Unauthorized (ou 403 Forbidden).
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        // Remove 'Bearer ' e retorna o token limpo.
        return authHeader.replace("Bearer ", "");
    }
}
