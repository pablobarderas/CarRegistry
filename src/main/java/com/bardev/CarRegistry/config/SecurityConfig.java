package com.bardev.CarRegistry.config;

import com.bardev.CarRegistry.filter.JwtAuthenticationFilter;
import com.bardev.CarRegistry.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .csrf(AbstractHttpConfigurer::disable) // disable cross origins
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sessions without state
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/login", "/signup", "/addUserImage/**", "/downloadUserImage/**").permitAll() // authorize post

                        // Brands authorizations
                        .requestMatchers(HttpMethod.POST, "/cars/**").permitAll() // authorize post
                        .requestMatchers(HttpMethod.PUT, "/cars/**").permitAll() // authorize post
                        .requestMatchers(HttpMethod.GET, "/cars/**").permitAll()   // authorize get
                        .requestMatchers(HttpMethod.DELETE, "/cars/**").permitAll()

                        // Brands authorizations
                        .requestMatchers(HttpMethod.POST, "/brands/**").permitAll() // authorize post
                        .requestMatchers(HttpMethod.GET, "/brands/**").permitAll()   // authorize get
                        .requestMatchers(HttpMethod.PUT, "/brands/**").permitAll() // authorize post
                        .requestMatchers(HttpMethod.DELETE, "/brands/**").permitAll()

                        .anyRequest().authenticated()
                )
                // Add provider previously defined
                .authenticationProvider(authenticationProvider()).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }


}
