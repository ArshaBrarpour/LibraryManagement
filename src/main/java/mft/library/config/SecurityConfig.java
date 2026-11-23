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

/**
 * تنظیمات امنیتی پروژه.
 *
 * علت خطای MemberService: Spring هیچ bean ای از نوع PasswordEncoder نداشت تا به constructor
 * تزریق کند. اینجا آن را می‌سازیم.
 *
 * روش ورود: HTTP Basic (ایمیل + رمز در هدر Authorization هر درخواست).
 * ساده‌ترین روش برای شروع است و نیازی به JWT نیست.
 */
@Configuration
public class SecurityConfig {

    // هش کردن رمز عبور با BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // به Spring Security می‌گوید کاربر را از جدول member پیدا کند
    @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository) {
        return username -> {
            Member member = memberRepository.findByEmail(username.trim().toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("کاربر پیدا نشد"));
            return User.withUsername(member.getEmail())
                    .password(member.getPassword())
                    .roles(member.getRole().name())   // USER یا ADMIN  ->  ROLE_USER / ROLE_ADMIN
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API ما stateless است و از cookie استفاده نمی‌کند، پس CSRF لازم نیست
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // ثبت‌نام برای همه آزاد است
                        .requestMatchers(HttpMethod.POST, "/api/members/register").permitAll()
                        // دیدن/جستجوی کتاب: هر کاربر واردشده (USER یا ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/books/**").authenticated()
                        // ثبت/ویرایش/حذف کتاب: فقط ADMIN
                        .requestMatchers("/api/books/**").hasRole("ADMIN")
                        // امانت، رزرو و جریمه: کاربر عادی
                        .requestMatchers("/api/loans/**", "/api/reservations/**", "/api/fines/**").hasRole("USER")
                        // بقیه (مثل /api/members/me) فقط نیاز به ورود دارند
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
