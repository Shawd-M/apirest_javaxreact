package com.quest.etna.controller;

import com.quest.etna.config.service.OrderProductService;
import com.quest.etna.model.OrderProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order-products")
public class OrderProductController {
    @Autowired
    private OrderProductService orderProductService;

    @GetMapping
    public List<OrderProduct> getAllOrderProducts() {
        return orderProductService.getAllOrderProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProduct> getOrderProductById(@PathVariable int id) {
        Optional<OrderProduct> orderProduct = orderProductService.getOrderProductById(id);
        return orderProduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public OrderProduct createOrderProduct(@RequestBody OrderProduct orderProduct) {
        return orderProductService.saveOrderProduct(orderProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderProduct> updateOrderProduct(@PathVariable int id, @RequestBody OrderProduct orderProductDetails) {
        Optional<OrderProduct> orderProduct = orderProductService.getOrderProductById(id);
        if (orderProduct.isPresent()) {
            OrderProduct orderProductToUpdate = orderProduct.get();
            orderProductToUpdate.setOrder(orderProductDetails.getOrder());
            orderProductToUpdate.setProduct(orderProductDetails.getProduct());
            orderProductToUpdate.setQuantity(orderProductDetails.getQuantity());
            orderProductToUpdate.setPrice(orderProductDetails.getPrice());
            return ResponseEntity.ok(orderProductService.saveOrderProduct(orderProductToUpdate));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderProduct(@PathVariable int id) {
        orderProductService.deleteOrderProduct(id);
        return ResponseEntity.noContent().build();
    }
}
