package br.com.fiap.appSori.config;

import br.com.fiap.appSori.config.security.filter.SecurityFilter;
import br.com.fiap.appSori.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SecurityConfig {
    private final SecurityFilter securityFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize

                        // 1. ROTAS PÚBLICAS (Login, Registro, Documentação)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // 2. ROTAS DE TESTES (TODAS CORRIGIDAS PARA hasAuthority)

                        // Rota ADMIN: Busca todos os testes (incluindo rascunhos, inativos, etc.)
                        .requestMatchers(HttpMethod.GET, "/api/testes/admin/todos").hasAuthority("ADMIN")

                        // Criação (POST): Apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/testes").hasAuthority("ADMIN")

                        // Exclusão (DELETE): Apenas ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/testes/**").hasAuthority("ADMIN")

                        // Busca e Leitura (GET): Rotas de usuário (lista, por ID, disponíveis, realizados)
                        // Todos os logados podem acessar estas rotas:
                        .requestMatchers(HttpMethod.GET, "/api/testes/**").hasAnyAuthority("ADMIN", "GESTOR_ORG", "CLIENTE")


                        // 3. ROTAS DE ADMIN/GESTOR (CORRIGIDAS PARA hasAuthority)
                        .requestMatchers(HttpMethod.GET, "/api/resultados/global").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tentativas/global").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/checkins/global").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAuthority("ADMIN")

                        // Rotas de Organizações
                        .requestMatchers("/api/organizacoes/**").hasAnyAuthority("ADMIN", "GESTOR_ORG")

                        // Atualização de perfil
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/{id}/perfil").authenticated()

                        // 4. Todas as outras rotas exigem autenticação (qualquer ROLE logado).
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info().title("API Psicosocial SORI").version("1.0"));
    }
}