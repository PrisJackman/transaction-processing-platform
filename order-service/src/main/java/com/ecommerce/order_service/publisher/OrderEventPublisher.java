package com.ecommerce.order_service.publisher;

import com.ecommerce.order_service.entity.Order;
import com.ecommerce.shared.event.OrderPlacedEvent;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final StreamBridge streamBridge;
    private final ModelMapper modelMapper;

    public OrderEventPublisher(StreamBridge streamBridge, ModelMapper modelMapper) {
        this.streamBridge = streamBridge;
        this.modelMapper = modelMapper;
    }

    public void publishOrderPlacedEvent(Order order) {

        OrderPlacedEvent event = modelMapper.map(order, OrderPlacedEvent.class);

        log.info("📤 [SRE Observability] Publicando evento: productId={}, quantity={}",
                event.getProductId(), event.getQuantity());

        long startTime = System.currentTimeMillis();
        boolean sent = streamBridge.send("orderPlaced-out-0", event);
        long duration = System.currentTimeMillis() - startTime;

        if (sent) {
            log.info("✅ [SRE Observability] Evento publicado en {}ms - topic: order-placed-topic", duration);
        } else {
            log.error("❌ [SRE Observability] FALLO publicación evento - duración: {}ms", duration);
        }
    }
}
