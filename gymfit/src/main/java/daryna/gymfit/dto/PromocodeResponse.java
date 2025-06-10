package daryna.gymfit.dto;

import java.util.List;

public record PromocodeResponse(int percent,
                                List<Long> applicableItemIds) {}