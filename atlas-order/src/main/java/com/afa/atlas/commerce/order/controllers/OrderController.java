package com.afa.atlas.commerce.order.controllers;

import com.afa.atlas.commerce.common.dto.PageResponse;
import com.afa.atlas.commerce.order.controllers.internal.ControllerConstants;
import com.afa.atlas.commerce.order.dto.OrderResponse;
import com.afa.atlas.commerce.order.dto.OrderSaveRequest;
import com.afa.atlas.commerce.order.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.afa.atlas.commerce.order.controllers.internal.ControllerConstants.ORDERS;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(ORDERS)
@Tag(name = "Orders controller", description = "Operations with orders")
public class OrderController {

    private final OrderService service;

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable final UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public PageResponse<OrderResponse> getAll(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        return service.getAll(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ControllerConstants.ROLE_ADMIN})
    public OrderResponse create(@RequestBody final OrderSaveRequest request) {
        return service.create(request);
    }
}
