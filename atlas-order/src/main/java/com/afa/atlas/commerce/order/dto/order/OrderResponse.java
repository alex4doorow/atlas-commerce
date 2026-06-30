package com.afa.atlas.commerce.order.dto.order;

import com.afa.atlas.commerce.common.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Order info")
public record OrderResponse(

        @Schema(description = "Order id")
        UUID id,

        @Schema(description = "Order number")
        String orderNumber,

        @Schema(description = "Order status")
        OrderStatus status,

        @Schema(description = "Total amount")
        BigDecimal totalAmount,

        @Schema(description = "Created date/time")
        OffsetDateTime createdAt,

        @Schema(description = "Updated date/time")
        OffsetDateTime updatedAt,

        @Schema(description = "Order items")
        List<OrderItemResponse> items
) {
}
