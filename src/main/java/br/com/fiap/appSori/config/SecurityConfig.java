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

                        // 2. ROTAS DE TESTES (AJUSTADO PARA ABRANGER BUSCA POR ID)

                        // Criação (POST): Apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/testes").hasRole("ADMIN")

                        // Busca e Leitura (GET): Se você quer que TODOS (logados) vejam,
                        // a configuração 'anyRequest().authenticated()' no final já serve.
                        // Mas vamos garantir que GESTOR_ORG e ADMIN possam ver (e CLIENTE, se necessário).
                        // Se o CLIENTE PODE VER, use hasAnyRole. Se apenas ADMIN/GESTOR_ORG podem ver, use as roles específicas.
                        .requestMatchers(HttpMethod.GET, "/api/testes").hasAnyRole("ADMIN", "GESTOR_ORG", "CLIENTE")

                        // Busca por ID (GET com curinga): Essencial para buscar testes específicos.
                        .requestMatchers(HttpMethod.GET, "/api/testes/**").hasAnyRole("ADMIN", "GESTOR_ORG", "CLIENTE")

                        // Exclusão (DELETE): Apenas ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/testes/**").hasRole("ADMIN")

                        // 3. ROTAS DE ADMIN/GESTOR
                        .requestMatchers(HttpMethod.GET, "/api/resultados/global").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tentativas/global").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/checkins/global").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")

                        // Rotas de Organizações
                        .requestMatchers("/api/organizacoes/**").hasAnyRole("ADMIN", "GESTOR_ORG")

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