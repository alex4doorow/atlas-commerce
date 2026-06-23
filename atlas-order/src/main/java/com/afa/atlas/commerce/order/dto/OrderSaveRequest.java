package com.afa.atlas.commerce.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Order save request")
public record OrderSaveRequest(

        List<CreateOrderItemRequest>items

) {
}
