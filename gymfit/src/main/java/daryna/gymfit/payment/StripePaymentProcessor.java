package daryna.gymfit.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.LineItemCollection;
import com.stripe.model.checkout.Session;
import com.stripe.model.LineItem;
import com.stripe.param.checkout.SessionListLineItemsParams;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.enums.GymMembershipType;
import daryna.gymfit.services.ClientService;
import daryna.gymfit.services.GymMembershipService;
import daryna.gymfit.services.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripePaymentProcessor {

    private final ClientService clientService;
    private final MembershipService membershipService;
    private final GymMembershipService gymMembershipService;

    public void processSuccessfulPayment(Session session) {
        String email = session.getCustomerEmail();
        List<LineItem> lineItems = fetchLineItems(session.getId());
        Client client = clientService.findByEmail(email);



        for (LineItem item : lineItems) {

            Map<String, String> metadata = item.getPrice().getProductObject().getMetadata();
            if (metadata == null) {
                throw new RuntimeException("Missing metadata in Stripe item");
            }

            String type = item.getPrice().getProductObject().getMetadata().get("type");
            String templateId = item.getPrice().getProductObject().getMetadata().get("templateId");
            String passTypeValue = item.getPrice().getProductObject().getMetadata().get("passType");
            GymMembershipType passType = passTypeValue != null ? GymMembershipType.valueOf(passTypeValue) : null;
            long quantity = item.getQuantity();
            BigDecimal price= item.getPrice().getUnitAmountDecimal();

            switch (type) {
                case "PERSONAL" -> membershipService.createCoachMembership(client, Long.parseLong(templateId), (int)quantity);
                case "GROUP" -> membershipService.createGroupMembership(client, Long.parseLong(templateId), (int)quantity);
                case "GYM_ACCESS" -> gymMembershipService.createGymAccess(client, passType, (int)quantity, price);
            }
        }
    }

    private List<LineItem> fetchLineItems(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            SessionListLineItemsParams params = SessionListLineItemsParams.builder()
                    .addExpand("data.price.product")
                    .build();

            LineItemCollection lineItems = session.listLineItems(params);
            return lineItems.getData();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to fetch line items", e);
        }
    }
}
