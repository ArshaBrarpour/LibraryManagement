package mft.library.config;

import mft.library.model.Member;
import mft.library.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository) {
        return username -> {
            Member member = memberRepository.findByEmail(username.trim().toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("کاربر پیدا نشد"));
            return User.withUsername(member.getEmail())
                    .password(member.getPassword())
                    .roles(member.getRole().name())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/members/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**").authenticated()
                        .requestMatchers("/api/books/**").hasRole("ADMIN")
                        .requestMatchers("/api/loans/**", "/api/reservations/**", "/api/fines/**").hasRole("USER")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
