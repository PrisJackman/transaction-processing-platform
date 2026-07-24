package com.ecommerce.order_service.consumer;

import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.shared.event.InventoryResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class InventoryEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);
    private final OrderRepository orderRepository;

    public InventoryEventConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Bean
    public Consumer<InventoryResponseEvent> processInventoryResponse() {
        return response -> {
            log.info("📥 Respuesta recibida de Inventario para Orden ID: {} -> Estado: {}",
                    response.getOrderId(), response.getStatus());

            orderRepository.findById(response.getOrderId()).ifPresentOrElse(order -> {
                // Actualizamos el status final: SUCCESS o FAILED
                order.setStatus(response.getStatus());
                orderRepository.save(order);
                log.info("💾 Orden ID: {} actualizada exitosamente en Base de Datos a estado: {}",
                        order.getId(), order.getStatus());
            }, () -> {
                log.error("❌ No se encontró la Orden ID: {} en la base de datos.", response.getOrderId());
            });
        };
    }
}
