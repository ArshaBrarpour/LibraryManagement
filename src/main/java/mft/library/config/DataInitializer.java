package mft.library.config;

import mft.library.enums.Role;
import mft.library.model.Member;
import mft.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminName;
    private final String adminFamily;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(MemberRepository memberRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.admin.name}") String adminName,
                           @Value("${app.admin.family}") String adminFamily,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.password}") String adminPassword) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminName = adminName;
        this.adminFamily = adminFamily;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        String email = adminEmail.trim().toLowerCase();
        if (memberRepository.existsByEmail(email)) {
            return;
        }
        Member admin = new Member();
        admin.setName(adminName);
        admin.setFamily(adminFamily);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        memberRepository.save(admin);
    }
}
