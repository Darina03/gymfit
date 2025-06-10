package daryna.gymfit.services;

import daryna.gymfit.dao.CoachRatingRepository;
import daryna.gymfit.dao.PaymentSuccessRepository;
import daryna.gymfit.entities.PaymentSuccess;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentSuccessService {

    private final PaymentSuccessRepository paymentSuccessRepository;


    public void save(PaymentSuccess success) {
        paymentSuccessRepository.save(success);
    }

    public boolean existsBySessionId(String sessionId) {
        return paymentSuccessRepository.existsBySessionId(sessionId);
    }
}
