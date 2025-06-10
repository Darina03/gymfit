package daryna.gymfit.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import daryna.gymfit.dto.CartItemDto;
import daryna.gymfit.entities.PaymentSuccess;
import daryna.gymfit.payment.StripePaymentProcessor;
import daryna.gymfit.payment.StripeService;
import daryna.gymfit.services.GroupClassService;
import daryna.gymfit.services.GymMembershipService;
import daryna.gymfit.services.PaymentSuccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gymfit/checkout")
public class PaymentController {


    private final StripeService stripeService;
    private final StripePaymentProcessor stripePaymentProcessor;
    private final GymMembershipService gymMembershipService;
    private final GroupClassService groupClassService;
    private final PaymentSuccessService paymentSuccessService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;


    @PostMapping
    public ResponseEntity<?> checkout(@RequestBody List<CartItemDto> cartItems, Principal principal) throws StripeException {
        String clientEmail = principal.getName();

        boolean needsAllDayGymPass = cartItems.stream().anyMatch(item ->
                "PERSONAL".equalsIgnoreCase(item.membershipType())
        );

        boolean hasAllDayPassInCart = cartItems.stream().anyMatch(item ->
                "GYM_ACCESS".equalsIgnoreCase(item.membershipType()) &&
                        "ALL_DAY_PASS".equalsIgnoreCase(item.passType())
        );

        boolean hasActiveAllDayPass = gymMembershipService.hasActiveAllDayPass(clientEmail);

        if (needsAllDayGymPass && !hasAllDayPassInCart && !hasActiveAllDayPass) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Щоб придбати персональні тренування Ви маєте придбати цілодобовий абонемент на доступ до спортзалу!");
        }

        for (CartItemDto item : cartItems) {
            if ("GROUP".equalsIgnoreCase(item.membershipType())) {
                boolean requiresGymPass = groupClassService.ifGymPassRequired(item.id());
                if (requiresGymPass && !hasAllDayPassInCart && !hasActiveAllDayPass) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Один з абонементів в кошику потребє активного цілодобового абонемен6ту на доступ до спортзалу! ");
                }
            }
        }

        String sessionUrl = stripeService.createCheckoutSession(cartItems, clientEmail);
        return ResponseEntity.ok(Map.of("checkoutUrl", sessionUrl));
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }


        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null) {
                String email = session.getCustomerEmail();
                PaymentSuccess success = new PaymentSuccess();
                success.setSessionId(session.getId());
                success.setEmail(email);

                paymentSuccessService.save(success);
                stripePaymentProcessor.processSuccessfulPayment(session);
            }
        }

        return ResponseEntity.ok("");
    }

}
