package dev.flmelo.mangakeeper.backend.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((auth) -> auth
                                .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/colecoes/**", "/mangas/**", "/volumes/**", "/curtidas/colecao/**", "/usuarios/**").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/**").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/**").authenticated()
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /* Usuario em Memoria *
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails user = User
                .withUsername("felipe")
                .password(passwordEncoder().encode("Iorinho22"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
    */
}
