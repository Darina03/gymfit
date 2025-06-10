package daryna.gymfit.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "payment_success")
public class PaymentSuccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String email;

    private Instant timestamp = Instant.now();
}