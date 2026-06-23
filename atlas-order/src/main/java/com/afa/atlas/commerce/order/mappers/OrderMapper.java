package com.afa.atlas.commerce.order.mappers;

import com.afa.atlas.commerce.order.dto.OrderItemResponse;
import com.afa.atlas.commerce.order.dto.OrderResponse;
import com.afa.atlas.commerce.order.entities.order.Order;
import com.afa.atlas.commerce.order.entities.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrderMapper {

    OrderResponse toResponse(Order entity);

    List<OrderResponse> toResponseList(List<Order> entities);

    OrderItemResponse toResponse(OrderItem entity);

    List<OrderItemResponse> toItemResponseList(List<OrderItem> entities);
}