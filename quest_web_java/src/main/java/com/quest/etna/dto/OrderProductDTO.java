package com.quest.etna.dto;

import com.quest.etna.model.Product;

public class OrderProductDTO {
    private long id;
    private String name;
    private Product product;
    private Integer quantity;
    private Double price;

    // Getters et setters

    public String getName(String name) {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId(long id) {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}