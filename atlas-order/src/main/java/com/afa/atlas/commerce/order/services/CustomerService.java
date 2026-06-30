package com.afa.atlas.commerce.order.services;

import com.afa.atlas.commerce.common.exceptions.AtlasException;
import com.afa.atlas.commerce.order.dto.customer.CustomerResponse;
import com.afa.atlas.commerce.order.dto.customer.CustomerSaveRequest;
import com.afa.atlas.commerce.order.entities.customer.Customer;
import com.afa.atlas.commerce.order.mappers.CustomerMapper;
import com.afa.atlas.commerce.order.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.afa.atlas.commerce.common.enums.AtlasErrorCode.CUSTOMER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Transactional
    public CustomerResponse create(final CustomerSaveRequest request) {

        final Customer customer = mapper.toEntity(request);

        final Customer savedCustomer = repository.saveAndFlush(customer);

        return mapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(final UUID id) {
        final Customer customer = repository.findById(id)
                .orElseThrow(() -> new AtlasException(CUSTOMER_NOT_FOUND, "Customer not found: %s".formatted(id)));

        return mapper.toResponse(customer);
    }
}
