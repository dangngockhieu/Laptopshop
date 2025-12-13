package vn.techzone.khieu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

        private final JwtAuthenticationConverter jwtAuthenticationConverter;

        SecurityConfiguration(JwtAuthenticationConverter jwtAuthenticationConverter) {
                this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        // Config PasswordEncoder
        public PasswordEncoder passwordEncoder() {
                return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        }

        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(
                                                authz -> authz
                                                                // CÁC API Public
                                                                .requestMatchers("/api/auth/**").permitAll()
                                                                .requestMatchers("/api/payment/return").permitAll()
                                                                .requestMatchers("/api/products/**").permitAll()
                                                                .requestMatchers("/api/users/**").permitAll()
                                                                .requestMatchers("/storage/**").permitAll()

                                                                .requestMatchers(
                                                                                "/v3/api-docs",
                                                                                "/v3/api-docs/**",
                                                                                "/swagger-resources",
                                                                                "/swagger-resources/**",
                                                                                "/configuration/ui",
                                                                                "/configuration/security",
                                                                                "/swagger-ui/**",
                                                                                "/swagger-ui.html",
                                                                                "/webjars/swagger-ui/**")
                                                                .permitAll()

                                                                .requestMatchers("/").permitAll()
                                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .exceptionHandling(
                                                exceptions -> exceptions
                                                                .authenticationEntryPoint(
                                                                                new BearerTokenAuthenticationEntryPoint()) // 401
                                                                .accessDeniedHandler(
                                                                                new BearerTokenAccessDeniedHandler()))
                                .formLogin(f -> f.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }
}
