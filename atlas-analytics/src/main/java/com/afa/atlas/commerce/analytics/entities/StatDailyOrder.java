package com.afa.atlas.commerce.analytics.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "stat_daily_orders",
        indexes = {
        },
        uniqueConstraints = {@UniqueConstraint(name = "uq_stat_daily_orders_stat_date", columnNames = "stat_date")}
)
@Getter
@Setter
@NoArgsConstructor
public class StatDailyOrder {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private LocalDate statDate;

    @Column(nullable = false)
    private Long ordersCount;

    @Column(nullable = false)
    private BigDecimal totalAmount;
}
