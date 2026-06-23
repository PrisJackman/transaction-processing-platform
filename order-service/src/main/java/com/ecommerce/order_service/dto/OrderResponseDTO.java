package com.ecommerce.order_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private String status;
    private LocalDateTime createdAt;
}
