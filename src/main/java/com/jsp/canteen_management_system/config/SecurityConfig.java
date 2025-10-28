package com.jsp.canteen_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// UserDetailsService import intentionally omitted — custom service is a component
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import com.jsp.canteen_management_system.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // Note: `CustomUserDetailsService` is a @Service component and will be picked up
    // by Spring. We don't expose another UserDetailsService bean to avoid duplicate
    // beans which interfere with authentication manager creation.

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Keep CSRF protection enabled for browser forms (default)
            .csrf().and()
            .authorizeHttpRequests(auth -> auth
                // Allow unauthenticated access to registration, login page, static resources and health
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/login", "/", "/index", "/css/**", "/js/**", "/static/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            // Enable form based login for browser users and keep basic auth for API clients
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                // After successful login redirect to the canteens list page
                .defaultSuccessUrl("/canteens", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}