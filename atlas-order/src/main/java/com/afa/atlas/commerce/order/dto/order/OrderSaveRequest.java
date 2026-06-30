package com.afa.atlas.commerce.order.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Order save request")
public record OrderSaveRequest(

        UUID customerId,
        List<CreateOrderItemRequest>items

) {
}
