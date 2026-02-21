package com.skybrisk.bookstore.service;

import com.skybrisk.bookstore.dto.OrderRequest;
import com.skybrisk.bookstore.dto.OrderResponse;
import com.skybrisk.bookstore.entity.*;
import com.skybrisk.bookstore.repository.BookRepository;
import com.skybrisk.bookstore.repository.OrderRepository;
import com.skybrisk.bookstore.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

        private final OrderRepository orderRepository;
        private final BookRepository bookRepository;
        private final UserRepository userRepository;

        public OrderService(OrderRepository orderRepository, BookRepository bookRepository,
                        UserRepository userRepository) {
                this.orderRepository = orderRepository;
                this.bookRepository = bookRepository;
                this.userRepository = userRepository;
        }

        @Transactional
        public OrderResponse placeOrder(OrderRequest request) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                List<OrderItem> orderItems = new ArrayList<>();
                BigDecimal totalOrderPrice = BigDecimal.ZERO;

                for (var itemReq : request.getItems()) {
                        Book book = bookRepository.findById(itemReq.getBookId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Book not found: " + itemReq.getBookId()));

                        if (book.getStockQuantity() < itemReq.getQuantity()) {
                                throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
                        }

                        // Deduct stock
                        book.setStockQuantity(book.getStockQuantity() - itemReq.getQuantity());
                        bookRepository.save(book);

                        BigDecimal itemPrice = book.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                        totalOrderPrice = totalOrderPrice.add(itemPrice);

                        orderItems.add(OrderItem.builder()
                                        .book(book)
                                        .quantity(itemReq.getQuantity())
                                        .price(book.getPrice())
                                        .build());
                }

                Order order = Order.builder()
                                .user(user)
                                .totalPrice(totalOrderPrice)
                                .status(OrderStatus.PENDING)
                                .paymentStatus("PENDING") // Mock payment status
                                .build();

                for (OrderItem item : orderItems) {
                        item.setOrder(order);
                }
                order.setOrderItems(orderItems);

                Order savedOrder = orderRepository.save(order);
                return mapToResponse(savedOrder);
        }

        public List<OrderResponse> getAllOrders() {
                return orderRepository.findAll().stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public List<OrderResponse> getOrdersByUser() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return orderRepository.findByUser(user).stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public OrderResponse getOrderById(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
                return mapToResponse(order);
        }

        @Transactional
        public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
                order.setStatus(status);
                return mapToResponse(orderRepository.save(order));
        }

        private OrderResponse mapToResponse(Order order) {
                return OrderResponse.builder()
                                .id(order.getId())
                                .customerName(order.getUser().getName())
                                .customerEmail(order.getUser().getEmail())
                                .totalPrice(order.getTotalPrice())
                                .status(order.getStatus())
                                .paymentStatus(order.getPaymentStatus())
                                .orderDate(order.getOrderDate())
                                .items(order.getOrderItems().stream()
                                                .map(item -> OrderResponse.OrderItemResponse.builder()
                                                                .bookTitle(item.getBook().getTitle())
                                                                .quantity(item.getQuantity())
                                                                .price(item.getPrice())
                                                                .build())
                                                .collect(Collectors.toList()))
                                .build();
        }
}
