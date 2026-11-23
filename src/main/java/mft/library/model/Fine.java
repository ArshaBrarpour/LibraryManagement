package mft.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "fine")
@NoArgsConstructor
@Getter
@Setter
public class Fine implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // تغییر: نوع amount از BigDecimal به long عوض شد.
    // مبلغ جریمه عدد صحیح است (روزهای دیرکرد × مبلغ ثابت روزانه) و
    // با long می‌شود در FineService مستقیم با += جمع زد.
    // علت خطای قبلی: روی BigDecimal عملگر += کار نمی‌کند.
    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private boolean paid;
}
