package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF для API
                .authorizeHttpRequests(authz -> authz
                        // Пути для управления пользователями (создание, назначение ролей) - только для ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Пути для модераторских функций - для MODERATOR и ADMIN
                        .requestMatchers("/api/moderator/**").hasAnyRole("MODERATOR", "ADMIN")

                        // Пути для функций (CRUD, операции) - требуют аутентификации (любая роль)
                        .requestMatchers("/api/functions/**").authenticated()

                        // Пути для поиска - требуют аутентификации
                        .requestMatchers("/api/search/**").authenticated()

                        // Все остальные разрешены всем (например, /actuator/health)
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("Function Storage Realm")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        User.UserBuilder userBuilder = User.builder().passwordEncoder(passwordEncoder()::encode);
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(userBuilder
                .username("user")
                .password("password")
                .roles("USER")
                .build());
        manager.createUser(userBuilder
                .username("moderator")
                .password("moderator")
                .roles("MODERATOR")
                .build());
        manager.createUser(userBuilder
                .username("admin")
                .password("admin")
                .roles("ADMIN")
                .build());
        return manager;
    }
}