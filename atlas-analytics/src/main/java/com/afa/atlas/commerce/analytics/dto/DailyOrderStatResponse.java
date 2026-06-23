package com.afa.atlas.commerce.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Daily order statistics")
public record DailyOrderStatResponse(

        @Schema(description = "Statistics date", example = "2026-06-22")
        LocalDate statDate,

        @Schema(description = "Orders count", example = "4")
        long ordersCount,

        @Schema(description = "Total orders amount", example = "311000.00")
        BigDecimal totalAmount
) {
}