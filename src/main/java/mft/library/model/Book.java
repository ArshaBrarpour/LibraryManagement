package mft.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// تغییر: @SuperBuilder و @AllArgsConstructor حذف شدند.
// جایی از پروژه از builder استفاده نمی‌کرد و وجود @SuperBuilder می‌تواند
// MapStruct را موقع ساخت mapper دچار مشکل کند. MapStruct با
// constructor خالی + setter ها کار می‌کند.
@Entity
@Table(name = "book")
@NoArgsConstructor
@Getter
@Setter
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, unique = true)
    private String isbn;

    // تعداد کل نسخه‌هایی که کتابخانه دارد
    @Column(name = "total_copies", nullable = false)
    private int totalCopies;

    // تعداد نسخه‌هایی که همین الان آزاد و قابل امانت است
    @Column(name = "available_copies", nullable = false)
    private int availableCopies;
}
