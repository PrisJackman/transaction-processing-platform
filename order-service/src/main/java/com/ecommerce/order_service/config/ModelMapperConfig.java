package com.ecommerce.order_service.config;

import com.ecommerce.order_service.dto.OrderRequestDTO;
import com.ecommerce.order_service.entity.Order;
import org.modelmapper.ModelMapper;
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

        // Solo especificamos qué IGNORAR (lo demás se mapea automáticamente)
        modelMapper.typeMap(OrderRequestDTO.class, Order.class)
                .addMappings(mapper -> {
                    mapper.skip(Order::setId);        // ¡CRÍTICO!
                    mapper.skip(Order::setStatus);     // ¡CRÍTICO!
                    mapper.skip(Order::setCreatedAt);  // ¡CRÍTICO!
                });

        return modelMapper;
    }
}
