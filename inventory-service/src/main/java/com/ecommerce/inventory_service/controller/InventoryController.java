package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.dto.ProductResponseDTO;
import com.ecommerce.inventory_service.entity.Product;
import com.ecommerce.inventory_service.service.InventoryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor

public class InventoryController {
    private final InventoryService inventoryService;
    private final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @GetMapping("/products")
    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "getInventoryFallback")
    public List<Product> getAllProducts() {
        log.info("Request to get all products");
        return inventoryService.getAllProducts();
    }

    @GetMapping
    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "getInventoryFallback")
    public List<Product> getAll() {
        log.info("Request to get all products (legacy endpoint)");
        return inventoryService.getAllProducts();
    }

    @GetMapping("/{id}/check")
    public boolean checkStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("Stock check request - productId: {}, quantity: {}", id, quantity);
        return inventoryService.checkStock(id,quantity);
    }

    @GetMapping("/{id}/dto")
    public ProductResponseDTO getProductDTO(@PathVariable Long id) {
        log.info("Product DTO request for id: {}", id);
        return inventoryService.getProductDTO(id);
    }
    
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return inventoryService.getProduct(id);
    }


    public List<Product> getInventoryFallback(Throwable e) {
        log.error("[SRE Observability] Circuit Breaker ABIERTO o servicio caído. Activando Fallback de resiliencia. Motivo: {}", e.getMessage());

        // Creamos un producto simulado usando la entidad real para que la UI de React no se rompa
        Product degradedProduct = new Product();
        degradedProduct.setId(0L);
        degradedProduct.setName("Catálogo en Modo Degradado (Resilience4j)");
        degradedProduct.setPrice(0.0);
        degradedProduct.setStock(0);

        return List.of(degradedProduct);
    }
}
