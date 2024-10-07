package com.quest.etna.controller;

import com.quest.etna.config.exception.ForbiddenAccessException;
import com.quest.etna.config.exception.ResourceNotFoundException;
import com.quest.etna.config.service.OrderService;
import com.quest.etna.dto.OrderDTO;
import com.quest.etna.dto.OrderProductDTO;
import com.quest.etna.dto.UserDTO;
import com.quest.etna.model.Address;
import com.quest.etna.model.Order;
import com.quest.etna.model.User;
import com.quest.etna.repositories.OrderRepository;
import com.quest.etna.repositories.ProductRepository;
import com.quest.etna.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.quest.etna.response.ErrorResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

        private OrderDTO convertToOrderDTO(Order order) {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setOrderDate(order.getOrderDate());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setUser(convertToUserResponseDTO(order.getUser()));

            List<OrderProductDTO> orderItemDTOs = order.getOrderItems().stream()
                    .map(orderItem -> {
                        OrderProductDTO itemDTO = new OrderProductDTO();
                        itemDTO.setId(orderItem.getProduct().getId());
                        itemDTO.setName(orderItem.getProduct().getName());
                        itemDTO.setQuantity(orderItem.getQuantity());
                        itemDTO.setPrice(orderItem.getPrice());
                        return itemDTO;
                    })
                    .collect(Collectors.toList());
            dto.setOrderItems(orderItemDTOs);

            return dto;
        }

        private UserDTO convertToUserResponseDTO(User user) {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setRole(user.getRole());
            return dto;
        }


        @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable int userId) {
        List<OrderDTO> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        // Vérifier si l'utilisateur est null dans la requête
        if (order.getUser() == null) {
            order.setUser(null);
        }

        // Reste du code existant
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("La commande ne contient pas de produits"));
        }

        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(savedOrder);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable int id, @RequestBody Order orderDetails) {
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            Order orderToUpdate = order.get();
            orderToUpdate.setOrderDate(orderDetails.getOrderDate());
            orderToUpdate.setTotalAmount(orderDetails.getTotalAmount());
            orderToUpdate.setUser(orderDetails.getUser());
            orderToUpdate.setOrderItems(orderDetails.getOrderItems());
            return ResponseEntity.ok(orderService.saveOrder(orderToUpdate));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

