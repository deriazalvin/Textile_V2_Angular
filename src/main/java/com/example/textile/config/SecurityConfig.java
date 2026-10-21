package com.example.textile.config;

import com.example.textile.security.CurrentUserDetailsService;
import com.example.textile.security.RestAccessDeniedHandler;
import com.example.textile.security.RestAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                                      CurrentUserDetailsService userDetailsService,
                                                      RestAuthenticationEntryPoint authenticationEntryPoint,
                                                      RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Pour admin
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                // @Toutlemonde
                .requestMatchers(HttpMethod.GET, "/api/**")
                        .hasAnyRole("ADMIN", "MANAGER", "OPERATOR", "USER")
                
                .requestMatchers("/api/clients/**", "/api/produits/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                // Workflow de production, qualite, livraison : operations quotidiennes.
                .requestMatchers("/api/orders/*/production/**", "/api/orders/*/quality-control",
                        "/api/orders/*/delivery")
                        .hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                // Annulation et administration des commandes : gestion metier.
                .requestMatchers("/api/orders/*/cancel").hasAnyRole("ADMIN", "MANAGER")
                // Creation / modification / suppression d'une commande.
                .requestMatchers(HttpMethod.POST, "/api/orders").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))
            .userDetailsService(userDetailsService);

        return http.build();
    }

    // ---------------------------------------------------- Interface Thymeleaf

    @Bean
    public SecurityFilterChain webSecurityFilterChain(
            HttpSecurity http,
            CurrentUserDetailsService userDetailsService,
            org.springframework.security.web.access.AccessDeniedHandler accessDeniedPage) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                // Administration : ADMIN.
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Referential clients / produits : gestion metier.
                .requestMatchers("/clients/**", "/produits/**", "/commandes/nouvelle",
                        "/commandes/*/modifier").hasAnyRole("ADMIN", "MANAGER")
                // Production, qualite, livraison : operations quotidiennes.
                .requestMatchers("/commandes/*/production/**", "/commandes/*/qualite/**",
                        "/commandes/*/livraison/**").hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                // Tout le reste est visible par les roles authentifies (USER = consultation).
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll())
            .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedPage))
            .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public org.springframework.security.web.access.AccessDeniedHandler accessDeniedPage() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(
                    "<!DOCTYPE html><html lang=\"fr\"><head><meta charset=\"UTF-8\">"
                            + "<title>Acces refuse</title></head><body>"
                            + "<h1>403 - Acces refuse</h1>"
                            + "<p>Votre role ne vous autorise pas cette operation.</p>"
                            + "<p><a href=\"/dashboard\">Retour au tableau de bord</a></p>"
                            + "</body></html>");
        };
    }
}