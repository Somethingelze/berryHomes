package net.berryhomes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/admin/settings/**").hasRole("ADMIN")
                .requestMatchers("/admin/audit/**").hasRole("ADMIN")
                .requestMatchers("/admin/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,
                        "/admin/contacts/*/delete",
                        "/admin/blog/*/delete",
                        "/admin/tenant-website-documents/*/delete",
                        "/admin/projects/*/delete",
                        "/admin/projects/media/images/delete",
                        "/admin/projects/media/image/*/delete",
                        "/admin/projects/media/document/*/delete",
                        "/admin/projects/media/document/*/delete-ajax").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/css/**", "/js/**", "/images/**", "/documents/**").permitAll()
                .anyRequest().permitAll())
                .formLogin(form -> form.defaultSuccessUrl("/admin/dashboard", true).permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
