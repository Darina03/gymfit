package daryna.gymfit.controllers;

import daryna.gymfit.services.PaymentSuccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gymfit/payment-success")
public class PaymentSuccessController {

    private final PaymentSuccessService paymentSuccessService;

    @GetMapping("/verify-payment")
    public ResponseEntity<Boolean> verify(@RequestParam String sessionId) {
        boolean paid = paymentSuccessService.existsBySessionId(sessionId);
        return ResponseEntity.ok(paid);
    }
}
