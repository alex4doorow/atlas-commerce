package com.afa.atlas.commerce.analytics.services;

import com.afa.atlas.commerce.analytics.dto.DailyOrderStatResponse;
import com.afa.atlas.commerce.analytics.entities.StatDailyOrder;
import com.afa.atlas.commerce.analytics.repositories.StatDailyOrderRepository;
import com.afa.atlas.commerce.common.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyOrderStatService {

    private final StatDailyOrderRepository repository;

    @Transactional
    public void apply(final OrderCreatedEvent event) {
        final LocalDate statDate = Instant.parse(event.createdAt())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();

        final StatDailyOrder stat = repository.findByStatDate(statDate)
                .orElseGet(() -> {
                    final StatDailyOrder created = new StatDailyOrder();
                    created.setStatDate(statDate);
                    created.setOrdersCount(0L);
                    created.setTotalAmount(java.math.BigDecimal.ZERO);
                    return created;
                });

        stat.setOrdersCount(stat.getOrdersCount() + 1);
        stat.setTotalAmount(stat.getTotalAmount().add(event.totalAmount()));

        repository.save(stat);
    }

    @Transactional(readOnly = true)
    public List<DailyOrderStatResponse> findAll() {
        return repository.findAllByOrderByStatDateDesc()
                .stream()
                .map(stat -> new DailyOrderStatResponse(
                        stat.getStatDate(),
                        stat.getOrdersCount(),
                        stat.getTotalAmount()
                ))
                .toList();
    }
}