package com.ecommerce.inventory_service.consumer;

import com.ecommerce.inventory_service.repository.ProductRepository;
import com.ecommerce.inventory_service.entity.Product;
import com.ecommerce.shared.event.InventoryResponseEvent;
import com.ecommerce.shared.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Component
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final ProductRepository productRepository;

    public OrderEventConsumer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Bean
    public Function<OrderPlacedEvent, InventoryResponseEvent> reduceStock(){
        log.info("✅ [SRE Observability] Bean 'reduceStock' registrado - Escuchando topic: order-placed-topic");
        return this::processStockReduction;
    }

    @Transactional
    public InventoryResponseEvent processStockReduction(OrderPlacedEvent event) {
        long startTime = System.currentTimeMillis();

        log.info("========== [SRE OBSERVABILITY] ==========");
        log.info("📨 Evento recibido - OrderId: {}, ProductId: {}, Quantity: {}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        InventoryResponseEvent responseEvent = new InventoryResponseEvent();
        responseEvent.setOrderId(event.getOrderId());
        responseEvent.setProductId(event.getProductId());

        try {
            Optional<Product> productOptional = productRepository.findById(event.getProductId());

            if (productOptional.isEmpty()) {
                log.error("❌ [ALERTA] Producto {} no encontrado en BD de inventario", event.getProductId());
                responseEvent.setStatus("FAILED");
                responseEvent.setMessage("Producto no encontrado en inventario.");
                return responseEvent;
            }

            Product product = productOptional.get();
            log.info("📦 Producto encontrado: {} | Stock actual: {}", product.getName(), product.getStock());

            if (product.getStock() >= event.getQuantity()) {
                int oldStock = product.getStock();
                int newStock = oldStock - event.getQuantity();

                product.setStock(newStock);
                productRepository.save(product);

                long duration = System.currentTimeMillis() - startTime;
                log.info("✅ [ÉXITO] Stock descontado en {}ms", duration);
                log.info("📊 Historial Stock: {} → {} (decremento: {})",
                        oldStock, newStock, event.getQuantity());
                responseEvent.setStatus("SUCCESS");
                responseEvent.setMessage("Stock reservado exitosamente.");
            } else {
                log.error("❌ [ALERTA OPERATIVA] Stock insuficiente - Requerido: {}, Disponible: {}",
                        event.getQuantity(), product.getStock());
                responseEvent.setStatus("FAILED");
                responseEvent.setMessage("Stock insuficiente para procesar la orden.");
            }
        }catch (Exception e){
            log.error("💥 [ALERTA] Error crítico procesando inventario: {}", e.getMessage());
            responseEvent.setStatus("FAILED");
            responseEvent.setMessage("Error interno en el servicio de inventario: " + e.getMessage());
        }finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("⏱️ [MÉTRICA] Tiempo de procesamiento: {}ms | Estado final: {}", duration, responseEvent.getStatus());
            log.info("=========================================\n");
        }

        log.info("=========================================\n");
        return responseEvent;
    }
}
