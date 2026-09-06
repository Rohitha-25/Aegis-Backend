package com.agenticai.aegisagent.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://aegis-avenger.vercel.app"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        grantedAuthoritiesConverter.setAuthoritiesClaimName("permissions");
        grantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/error")
                                .permitAll()

                                // ---------------------
                                //      AGENT APIs
                                // ---------------------

                                // READ
                                .requestMatchers(HttpMethod.GET, "/api/agents", "/api/agents/**")
                                .hasAuthority("SCOPE_agent:read")

                                // CREATE
                                .requestMatchers(HttpMethod.POST, "/api/agents")
                                .hasAuthority("SCOPE_agent:manage")

                                // Lifecycle Management
                                .requestMatchers(HttpMethod.PUT, "/api/agents/**")
                                .hasAuthority("SCOPE_agent:manage")

                                // ---------------------
                                //     WORKLOAD APIs
                                // ---------------------

                                // Workload Registration, Activation, Suspension, Revocation
                                .requestMatchers("/api/workloads/**")
                                .hasAuthority("SCOPE_agent:manage")

                                // ---------------------
                                //      TOOL APIs
                                // ---------------------

                                // Tool Registration, Activation, Suspension, Disablement
                                .requestMatchers("/api/tools/**")
                                .hasAuthority("SCOPE_agent:manage")

                                // ---------------------
                                //       TASK APIs
                                // ---------------------

                                // Task Creation and Lifecycle
                                .requestMatchers("/api/tasks/**")
                                .hasAuthority("SCOPE_agent:execute")

                                // ---------------------
                                //       DOC APIs
                                // ---------------------

                                // Document Creation
                                .requestMatchers(HttpMethod.POST, "/api/documents/upload")
                                .hasAuthority("SCOPE_agent:manage")

                                // Document Search Response
                                .requestMatchers(HttpMethod.GET, "/api/documents")
                                .authenticated()

                                // ---------------------
                                //      AUDIT APIs
                                // ---------------------

                                // Audit Event Management
                                .requestMatchers("/api/audit/**")
                                .hasAuthority("SCOPE_agent:manage")

                                // ---------------------
                                //       PAM APIs
                                // ---------------------

                                // PAM - JIT + LeastPrivilege + Audit
                                .requestMatchers("/api/pam/**")
                                .hasAuthority("SCOPE_agent:manage")

                                // ---------------------
                                //       RISK APIs
                                // ---------------------

                                // Risk Assessment
                                .requestMatchers("/api/risk/**")
                                .hasAuthority("SCOPE_agent:manage")

                                .anyRequest()
                                .authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                );

        return http.build();
    }
}
