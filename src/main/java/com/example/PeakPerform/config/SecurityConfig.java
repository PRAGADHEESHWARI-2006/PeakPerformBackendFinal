package com.example.PeakPerform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;

import com.example.PeakPerform.security.CustomUserDetailsService;
import com.example.PeakPerform.security.JwtFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtFilter jwtFilter;

        private final CustomUserDetailsService customUserDetailsService;

        public SecurityConfig(JwtFilter jwtFilter,
                        CustomUserDetailsService customUserDetailsService) {
                this.jwtFilter = jwtFilter;
                this.customUserDetailsService = customUserDetailsService;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration)
                        throws Exception {

                return configuration.getAuthenticationManager();
        }

        @Bean
        public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {

                org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

                configuration.setAllowedOrigins(java.util.List.of(
                                "http://localhost:5501",
                                "http://127.0.0.1:5501",
                                "http://localhost:5500",
                                "http://127.0.0.1:5500","https://pragadheeshwari-2006.github.io"));

                configuration.setAllowedMethods(java.util.List.of(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "PATCH",
                                "OPTIONS"));

                configuration.setAllowedHeaders(java.util.List.of("*"));
                configuration.setAllowCredentials(true);

                org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);

                provider.setPasswordEncoder(passwordEncoder());

                return provider;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/",
                                                                "/index.html",
                                                                "/register.html",
                                                                "/dashboard.html",
                                                                "/objectives.html",
                                                                "/keyresults.html",
                                                                "/cycles.html",
                                                                "/checkins.html",
                                                                "/profile.html",
                                                                "/reports.html",
                                                                "/css/**",
                                                                "/js/**")
                                                .permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                                                .permitAll()

                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtFilter,
                                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

}
