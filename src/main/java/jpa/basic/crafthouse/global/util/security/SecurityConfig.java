package jpa.basic.crafthouse.global.util.security;

import jpa.basic.crafthouse.global.util.jwt.JwtAuthenticationEntryPoint;
import jpa.basic.crafthouse.global.util.jwt.JwtAuthenticationFilter;
import jpa.basic.crafthouse.global.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 케이스 3에서 추가

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/assets/**", "/img/**", "/error", "/favicon.ico",
                        "/h2-console/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/api/auth/login",
                        "/api/auth/signup",
                        "/api/auth/reissue"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/check-duplicate").permitAll()
                .anyRequest().authenticated()
            )

            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )

            .addFilterBefore(
                    new JwtAuthenticationFilter(jwtTokenProvider),
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}