package com.ecommerce.inventory_service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    @Bean
    public Counter stockReductionSuccessCounter(MeterRegistry registry) {
        return Counter.builder("inventory.stock.reduction.success")
                .description("Successful stock reductions")
                .register(registry);
    }

    @Bean
    public Counter stockReductionFailureCounter(MeterRegistry registry) {
        return Counter.builder("inventory.stock.reduction.failure")
                .description("Failed stock reductions")
                .register(registry);
    }

    @Bean
    public Timer stockReductionTimer(MeterRegistry registry) {
        return Timer.builder("inventory.stock.reduction.duration")
                .description("Time taken to process stock reduction")
                .register(registry);
    }
}
