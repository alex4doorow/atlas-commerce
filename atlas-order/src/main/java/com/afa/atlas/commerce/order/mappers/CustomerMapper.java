package com.afa.atlas.commerce.order.mappers;

import com.afa.atlas.commerce.order.dto.customer.CustomerResponse;
import com.afa.atlas.commerce.order.dto.customer.CustomerSaveRequest;
import com.afa.atlas.commerce.order.entities.customer.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CustomerMapper {

    CustomerResponse toResponse(Customer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toEntity(CustomerSaveRequest request);

}