package daryna.gymfit.dao;

import daryna.gymfit.entities.PaymentSuccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentSuccessRepository extends JpaRepository<PaymentSuccess,Long> {

    boolean existsBySessionId(String sessionId);
}
