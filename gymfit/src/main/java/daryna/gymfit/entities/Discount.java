package daryna.gymfit.entities;

import daryna.gymfit.entities.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DiscountType type;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "promocode" )
    private String promocode;

    @Column(name = "min_quantity")
    private Integer minQuantity;

    @ManyToOne
    @JoinColumn(name = "id_field")
    private Field field;

    @ManyToOne
    @JoinColumn(name = "id_coach")
    private Coach coach;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_first_purchase")
    private boolean isFirstPurchase;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_shown")
    private boolean isShown;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
