package com.skybrisk.bookstore.dto;

import com.skybrisk.bookstore.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String paymentStatus;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemResponse {
        private String bookTitle;
        private Integer quantity;
        private BigDecimal price;
    }
}
