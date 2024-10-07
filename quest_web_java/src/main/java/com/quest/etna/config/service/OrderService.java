package com.quest.etna.config.service;

import com.quest.etna.dto.OrderDTO;
import com.quest.etna.dto.OrderProductDTO;
import com.quest.etna.dto.UserDTO;
import com.quest.etna.model.Order;
import com.quest.etna.model.OrderProduct;
import com.quest.etna.model.Product;
import com.quest.etna.model.User;
import com.quest.etna.repositories.OrderProductRepository;
import com.quest.etna.repositories.OrderRepository;
import com.quest.etna.repositories.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Order> getOrdersByUserId(int userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order createOrder(Order order) {
        // Créer une nouvelle liste pour stocker les instances de OrderProduct
        List<OrderProduct> orderProducts = new ArrayList<>();

        // Associer les instances de Product aux instances de OrderProduct
        for (OrderProduct orderProduct : order.getOrderItems()) {
            Product product = productRepository.findById(orderProduct.getProduct().getId()).get();
            orderProduct.setProduct(product);
            orderProducts.add(orderProduct);
        }

        // Créer une nouvelle instance de Order avec la nouvelle liste d'orderProducts
        Order newOrder = new Order();
        newOrder.setUser(order.getUser() != null ? order.getUser() : null);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setOrderItems(orderProducts);

        // Reste du code existant
        Order savedOrder = orderRepository.save(newOrder);

        // Mettre à jour la quantité de stock des produits
        for (OrderProduct orderProduct : savedOrder.getOrderItems()) {
            Product product = productRepository.findById(orderProduct.getProduct().getId()).get();
            product.setStockQuantity(product.getStockQuantity() - orderProduct.getQuantity());
            entityManager.merge(product);
        }

        return savedOrder;
    }


    private OrderDTO convertToDTO(Order order) {
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



    public Optional<Order> getOrderById(int id) {
        return orderRepository.findById(id);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> findByUserId(int userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

}
