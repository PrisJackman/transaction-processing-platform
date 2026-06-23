package com.ecommerce.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {
    @NotNull(message = "Product ID es requerido")
    private Long productId;

    @NotNull(message = "Quantity es requerida")
    @Min(value = 1, message = "Quantity debe ser al menos 1")
    private Integer quantity;
}
