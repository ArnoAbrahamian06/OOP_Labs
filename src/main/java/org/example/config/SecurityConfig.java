package org.example.config;

import org.example.entity.Role;

import org.example.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF для API
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                        // Пути для управления пользователями (создание, назначение ролей) - только для ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Пути для модераторских функций - для MODERATOR и ADMIN
                        .requestMatchers("/api/moderator/**").hasAnyRole("MODERATOR", "ADMIN")

                        // Пути для функций (CRUD, операции) - требуют аутентификации (любая роль)
                        .requestMatchers("/api/functions/**").authenticated()

                        // Пути для поиска - требуют аутентификации
                        .requestMatchers("/api/search/**").authenticated()

                        // Разрешаем аутентификацию без авторизации
                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger UI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Все остальные разрешены всем (например, /actuator/health)
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        User.UserBuilder userBuilder = User.builder(); //.passwordEncoder(passwordEncoder()::encode);
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(userBuilder
//                .username("user")
//                .password("password")
//                .roles(Role.USER)
//                .build());
//        manager.createUser(userBuilder
//                .username("moderator")
//                .password("moderator")
//                .roles("MODERATOR")
//                .build());
//        manager.createUser(userBuilder
//                .username("admin")
//                .password("admin")
//                .roles("ADMIN")
//                .build());
//        return manager;
//    }
}