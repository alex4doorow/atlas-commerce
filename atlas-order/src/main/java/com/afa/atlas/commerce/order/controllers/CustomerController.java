package com.afa.atlas.commerce.order.controllers;

import com.afa.atlas.commerce.order.controllers.internal.ControllerConstants;
import com.afa.atlas.commerce.order.dto.customer.CustomerResponse;
import com.afa.atlas.commerce.order.dto.customer.CustomerSaveRequest;
import com.afa.atlas.commerce.order.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ControllerConstants.CUSTOMERS)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody final CustomerSaveRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable final UUID id) {
        return service.getById(id);
    }
}