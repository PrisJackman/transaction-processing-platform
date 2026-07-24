package com.ecommerce.order_service.config;

import com.ecommerce.order_service.dto.OrderRequestDTO;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.shared.event.OrderPlacedEvent;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configuración ESTRICTA
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true);

        // Configuración para OrderRequestDTO → Order
        modelMapper.typeMap(OrderRequestDTO.class, Order.class)
                .addMappings(mapper -> {
                    mapper.skip(Order::setId);
                    mapper.skip(Order::setStatus);
                    mapper.skip(Order::setCreatedAt);
                });

        // Configuración específica para Order → OrderPlacedEvent
        modelMapper.addMappings(new PropertyMap<Order, OrderPlacedEvent>() {
            @Override
            protected void configure() {
                map().setOrderId(source.getId());
                map().setProductId(source.getProductId());
                map().setQuantity(source.getQuantity());
            }
        });

        return modelMapper;
    }
}
