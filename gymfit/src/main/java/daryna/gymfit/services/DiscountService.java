package daryna.gymfit.services;

import daryna.gymfit.dao.DiscountRepository;
import daryna.gymfit.dto.*;
import daryna.gymfit.entities.*;
import daryna.gymfit.entities.enums.DiscountType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final MembershipService membershipService;
    private final GymMembershipService gymMembershipService;
    private final GroupClassService groupClassService;
    private final CoachMembershipTemplateService coachMembershipTemplateService;

    public List<DiscountDto> findApplicableDiscounts(List<CartItemDto> cartItems, Client client) {
        LocalDate today = LocalDate.now();
        List<Discount> discounts = discountRepository.findByIsActiveTrue();
        Map<Long, ResolvedItem> resolvedItems = resolveCartItems(cartItems);
        int totalCartQuantity = cartItems.stream().mapToInt(CartItemDto::quantity).sum();
        List<DiscountDto> result = new ArrayList<>();

        for (Discount discount : discounts) {
            List<Long> applicableItemIds = new ArrayList<>();

            for (CartItemDto item : cartItems) {
                ResolvedItem resolved = resolvedItems.get(item.id());
                boolean isApplicable = switch (discount.getType()) {
                    case CART_QUANTITY ->  totalCartQuantity >= Optional.ofNullable(discount.getMinQuantity()).orElse(0);
                    case GYM_MEMBERSHIP_QUANTITY -> "GYM_ACCESS".equalsIgnoreCase(item.membershipType());
                    case PROMOCODE -> false;
                    case SPECIALTY -> discount.getField() != null &&
                            resolved != null &&
                            resolved.field() != null &&
                            discount.getField().getId().equals(resolved.field().getId());
                    case COACH_SPECIFIC -> discount.getCoach() != null &&
                            resolved != null &&
                            resolved.coach() != null &&
                            discount.getCoach().getId().equals(resolved.coach().getId());
                    case COACH_SPECIALTY_SPECIFIC -> discount.getCoach() != null && discount.getField() != null &&
                            resolved != null &&
                            resolved.coach() != null && resolved.field() != null &&
                            discount.getCoach().getId().equals(resolved.coach().getId()) &&
                            discount.getField().getId().equals(resolved.field().getId());
                    case DATE_RANGE -> today.isAfter(discount.getStartDate().minusDays(1)) &&
                            today.isBefore(discount.getEndDate().plusDays(1));
                    case NEW_CLIENT -> {
                        boolean hasMemberships = membershipService.existsByClient(client.getId())
                                || gymMembershipService.existsByClient(client.getId());
                        yield discount.isFirstPurchase() && !hasMemberships;
                    }
                };

                if (isApplicable) {
                    applicableItemIds.add(item.id());
                }
            }

            if (!applicableItemIds.isEmpty()) {
                result.add(new DiscountDto(discount.getTitle(), discount.getDiscountPercent(), applicableItemIds));
            }
        }

        return result;
    }



    public PromocodeResponse validatePromoCode(String code, List<CartItemDto> cartItems, Client client) {
        LocalDate today = LocalDate.now();

        Discount discount = discountRepository.findByPromocodeIgnoreCase(code)
                .filter(d -> d.getType() == DiscountType.PROMOCODE)
                .filter(Discount::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Промокод не існує!"));

        if (discount.getStartDate() != null && today.isBefore(discount.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Промокод недійсний!");
        }
        if (discount.getEndDate() != null && today.isAfter(discount.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Промокод недійсний!");
        }

        List<Long> applicableItemIds = cartItems.stream()
                .filter(item -> {
                    boolean matchesField = discount.getField() == null
                            || discount.getField().getId().equals(fetchFieldIdFromCartItem(item));
                    boolean matchesCoach = discount.getCoach() == null
                            || discount.getCoach().getId().equals(fetchCoachIdFromCartItem(item));
                    return matchesField && matchesCoach;
                })
                .map(CartItemDto::id)
                .toList();

        if (applicableItemIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Промокод не може бути застосованим ні до одного абонемента в кошику!");
        }

        if (discount.getMinQuantity() != null) {
            int totalApplicableQuantity = cartItems.stream()
                    .filter(item -> applicableItemIds.contains(item.id()))
                    .mapToInt(CartItemDto::quantity)
                    .sum();
            if (totalApplicableQuantity < discount.getMinQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Промокод потребує мінімальну кількість  " + discount.getMinQuantity());
            }
        }

        if (discount.isFirstPurchase()) {
            boolean hasMemberships = membershipService.existsByClient(client.getId())
                    || gymMembershipService.existsByClient(client.getId());
            if (hasMemberships) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Промокод тільки для першої покупки!");
            }
        }

        return new PromocodeResponse(discount.getDiscountPercent(), applicableItemIds);
    }

    private Long fetchCoachIdFromCartItem(CartItemDto item) {
        if ("COACH_MEMBERSHIP".equalsIgnoreCase(item.membershipType())) {
            return coachMembershipTemplateService.getCoachMembershipTemplateById(item.id()).getCoach().getId();
        } else if ("GROUP_CLASS".equalsIgnoreCase(item.membershipType())) {
            return groupClassService.getGroupClassById(item.id()).getCoach().getId();
        }
        return null;
    }

    private Long fetchFieldIdFromCartItem(CartItemDto item) {
        if ("COACH_MEMBERSHIP".equalsIgnoreCase(item.membershipType())) {
            return coachMembershipTemplateService.getCoachMembershipTemplateById(item.id()).getField().getId();
        } else if ("GROUP_CLASS".equalsIgnoreCase(item.membershipType())) {
            return groupClassService.getGroupClassById(item.id()).getField().getId();
        }
        return null;
    }


    private Map<Long, ResolvedItem> resolveCartItems(List<CartItemDto> cartItems) {
        Map<Long, ResolvedItem> resolvedMap = new HashMap<>();

        for (CartItemDto item : cartItems) {
            Coach coach = null;
            Field field = null;

            switch (item.membershipType()) {
                case "PERSONAL":
                    var template = coachMembershipTemplateService.getCoachMembershipTemplateById(item.id());
                    coach = template.getCoach();
                    field = template.getField();
                    break;

                case "GROUP":
                    var groupClass = groupClassService.getGroupClassById(item.id());
                    coach = groupClass.getCoach();
                    field = groupClass.getField();
                    break;

            }

            resolvedMap.put(item.id(), new ResolvedItem(item.id(), coach, field));
        }

        return resolvedMap;
    }

    public List<DiscountForDisplayingDto> findAllActive() {
        return discountRepository.findAllIsActiveAndIsShown();
    }
}

