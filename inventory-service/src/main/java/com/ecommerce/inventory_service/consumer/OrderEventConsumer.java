package com.ecommerce.inventory_service.consumer;

import com.ecommerce.inventory_service.repository.ProductRepository;
import com.ecommerce.inventory_service.entity.Product;
import com.ecommerce.shared.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final ProductRepository productRepository;

    public OrderEventConsumer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Bean
    public Consumer<OrderPlacedEvent> reduceStock(){
        log.info("✅ [SRE Observability] Bean 'reduceStock' registrado - Escuchando topic: order-placed-topic");
        return this::processStockReduction;
    }

    @Transactional
    public void processStockReduction(OrderPlacedEvent event) {
        long startTime = System.currentTimeMillis();

        log.info("========== [SRE OBSERVABILITY] ==========");
        log.info("📨 Evento recibido - ProductId: {}, Quantity: {}",
                event.getProductId(), event.getQuantity());

        Optional<Product> productOptional = productRepository.findById(event.getProductId());

        if (productOptional.isEmpty()) {
            log.error("❌ [ALERTA] Producto {} no encontrado en BD de inventario", event.getProductId());
            return;
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
        } else {
            log.error("❌ [ALERTA OPERATIVA] Stock insuficiente - Requerido: {}, Disponible: {}",
                    event.getQuantity(), product.getStock());
        }

        log.info("=========================================\n");
    }
}
