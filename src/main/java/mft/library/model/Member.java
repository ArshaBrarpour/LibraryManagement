package mft.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mft.library.enums.Role;

import java.io.Serializable;

@Entity
@Table(name = "member")
@NoArgsConstructor
@Getter
@Setter
public class Member implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String family;

    @Column(nullable = false, unique = true)
    private String email;

    // رمز عبور به‌صورت هش‌شده (BCrypt) ذخیره می‌شود، نه متن ساده
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
}
