package daryna.gymfit.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import daryna.gymfit.dto.CartItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class StripeService {

    private final String frontendUrl;

    @Autowired
    public StripeService(@Value("${frontend.url}") String frontendUrl,
                         @Value("${stripe.api.key}") String stripeSecretKey) {
        this.frontendUrl = frontendUrl;
        Stripe.apiKey = stripeSecretKey;
    }

    public String createCheckoutSession(List<CartItemDto> items, String clientEmail) throws StripeException {
        List<SessionCreateParams.LineItem> stripeItems = new ArrayList<>();

        for (CartItemDto item : items) {
            stripeItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.quantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("uah")
                                            .setUnitAmount(item.price().multiply(BigDecimal.valueOf(100.0)).longValue())
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.membershipType() + " абонемент")
                                                            .putMetadata("type", item.membershipType())
                                                            .putMetadata("templateId", item.id() != null ? String.valueOf(item.id()) : "")
                                                            .putMetadata("passType", item.passType()!=null ? item.passType() : "")
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addExpand("line_items.data.price.product")
                .addExpand("line_items")
                .setSuccessUrl(frontendUrl + "/success?sessionId={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel")
                .setCustomerEmail(clientEmail)
                .addAllLineItem(stripeItems)
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }


}
