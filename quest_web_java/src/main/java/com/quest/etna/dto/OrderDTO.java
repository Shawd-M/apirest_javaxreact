package com.quest.etna.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private int id;
    private UserDTO user;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private List<OrderProductDTO> orderItems;

    // Getters
    public int getId() {
        return id;
    }

    public UserDTO getUser() {
        return user;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public List<OrderProductDTO> getOrderItems() {
        return orderItems;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOrderItems(List<OrderProductDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
