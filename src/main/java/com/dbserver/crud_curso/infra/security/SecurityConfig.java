package com.dbserver.crud_curso.infra.security;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private SecurityFilter securityFilter;
    private static final String PROFESSOR_AUTORIDADE = "PROFESSOR";
    private static final String ALUNO_AUTORIDADE = "ALUNO";

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csfr -> csfr.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/aluno/reativar/**", "/professor/reativar/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/aluno", "/professor").permitAll()
                        .requestMatchers(HttpMethod.GET, "/professor/**", "/aluno/**", "/alunoCurso/**", "/professorCurso/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/alunoCurso/**").hasAuthority(ALUNO_AUTORIDADE)
                        .requestMatchers(HttpMethod.PATCH, "/alunoCurso/formar").hasAuthority(PROFESSOR_AUTORIDADE)
                        .requestMatchers(HttpMethod.PATCH, "/alunoCurso/trancarMatricula", "/alunoCurso/reativarMatricula").hasAuthority(ALUNO_AUTORIDADE)
                        

                        .requestMatchers(HttpMethod.POST, "/professorCurso/**").hasAuthority(PROFESSOR_AUTORIDADE)
                        .requestMatchers(HttpMethod.DELETE, "/professorCurso/**").hasAuthority(PROFESSOR_AUTORIDADE)
                        .requestMatchers(HttpMethod.PUT, "/professorCurso/**").hasAuthority(PROFESSOR_AUTORIDADE)

                        .requestMatchers("/aluno/**").hasAuthority(ALUNO_AUTORIDADE)
                        .requestMatchers("/curso/**", "/professor/**").hasAuthority(PROFESSOR_AUTORIDADE)
                        
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEnconder() {
        return new BCryptPasswordEncoder();
    }
}