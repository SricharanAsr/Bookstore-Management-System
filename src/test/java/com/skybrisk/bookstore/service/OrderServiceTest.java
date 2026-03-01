package com.skybrisk.bookstore.service;

import com.skybrisk.bookstore.dto.OrderItemRequest;
import com.skybrisk.bookstore.dto.OrderRequest;
import com.skybrisk.bookstore.dto.OrderResponse;
import com.skybrisk.bookstore.entity.*;
import com.skybrisk.bookstore.repository.BookRepository;
import com.skybrisk.bookstore.repository.OrderRepository;
import com.skybrisk.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void testPlaceOrder_Success() {
        User user = User.builder().email("test@example.com").name("Test User").role(Role.CUSTOMER).build();
        Book book = Book.builder().id(1L).title("Test Book").price(BigDecimal.valueOf(10)).stockQuantity(10).build();

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setBookId(1L);
        itemRequest.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItems(List.of(itemRequest));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.placeOrder(orderRequest);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(20), response.getTotalPrice());
        assertEquals(8, book.getStockQuantity());
        verify(bookRepository, times(1)).save(book);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testPlaceOrder_InsufficientStock() {
        User user = User.builder().email("test@example.com").name("Test User").role(Role.CUSTOMER).build();
        Book book = Book.builder().id(1L).title("Test Book").price(BigDecimal.valueOf(10)).stockQuantity(1).build();

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setBookId(1L);
        itemRequest.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItems(List.of(itemRequest));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.placeOrder(orderRequest));
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    void testUpdateOrderStatus_Success() {
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertEquals(OrderStatus.SHIPPED, response.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
