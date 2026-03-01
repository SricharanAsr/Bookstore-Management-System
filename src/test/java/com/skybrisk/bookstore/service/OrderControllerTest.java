package com.skybrisk.bookstore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skybrisk.bookstore.controller.OrderController;
import com.skybrisk.bookstore.dto.OrderRequest;
import com.skybrisk.bookstore.dto.OrderResponse;
import com.skybrisk.bookstore.entity.OrderStatus;
import com.skybrisk.bookstore.security.JwtAuthenticationFilter;
import com.skybrisk.bookstore.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for simple unit test of controller logic
class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser(roles = "CUSTOMER")
        void testPlaceOrder() throws Exception {
                OrderRequest request = new OrderRequest();
                request.setItems(new ArrayList<>());

                OrderResponse response = OrderResponse.builder()
                                .id(1L)
                                .totalPrice(BigDecimal.valueOf(100))
                                .status(OrderStatus.PENDING)
                                .build();

                when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.totalPrice").value(100));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateOrderStatus() throws Exception {
                OrderResponse response = OrderResponse.builder()
                                .id(1L)
                                .status(OrderStatus.SHIPPED)
                                .build();

                when(orderService.updateOrderStatus(any(), any())).thenReturn(response);

                mockMvc.perform(put("/api/orders/1/status")
                                .param("status", "SHIPPED"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("SHIPPED"));
        }
}
