package com.afa.atlas.commerce.analytics.repositories;

import com.afa.atlas.commerce.analytics.entities.StatDailyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatDailyOrderRepository extends JpaRepository<StatDailyOrder, UUID> {

    Optional<StatDailyOrder> findByStatDate(LocalDate statDate);

    List<StatDailyOrder> findAllByOrderByStatDateDesc();
}