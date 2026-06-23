package com.afa.atlas.commerce.analytics.controllers;

import com.afa.atlas.commerce.analytics.dto.DailyOrderStatResponse;
import com.afa.atlas.commerce.analytics.services.DailyOrderStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.afa.atlas.commerce.analytics.controllers.internal.ControllerConstants.ANALYTICS_ORDERS;

@Tag(name = "Order Analytics", description = "Order analytics API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ANALYTICS_ORDERS)
public class OrderAnalyticsController {

    private final DailyOrderStatService dailyOrderStatService;

    @Operation(summary = "Get daily order statistics")
    @GetMapping("/daily")
    public List<DailyOrderStatResponse> findDailyStats() {
        return dailyOrderStatService.findAll();
    }
}