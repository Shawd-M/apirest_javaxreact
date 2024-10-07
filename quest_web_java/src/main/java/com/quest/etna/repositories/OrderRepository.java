package com.quest.etna.repositories;

import com.quest.etna.dto.OrderDTO;
import com.quest.etna.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(int userId);
}