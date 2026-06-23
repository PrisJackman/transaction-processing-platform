package com.ecommerce.order_service.service;

import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.publisher.OrderEventPublisher;
import com.ecommerce.order_service.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }


    @Transactional
    public Order createOrder(Order order) {

        log.info("📦 Creando orden para productoId: {}, cantidad: {}",
                order.getProductId(), order.getQuantity());

        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);

        // Publicar evento
        eventPublisher.publishOrderPlacedEvent(savedOrder);

        return savedOrder;
    }

}
