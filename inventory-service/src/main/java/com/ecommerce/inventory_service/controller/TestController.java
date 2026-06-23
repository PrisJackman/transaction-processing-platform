package com.ecommerce.inventory_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {
    @GetMapping("/test")
    public String greeting() {
        return "Orders Service is UP and Running!";
    }

    // Este endpoint nos servirá para simular LATENCIA más adelante
    @GetMapping("/slow-process")
    public String slowEndpoint(@RequestParam(defaultValue = "0") int delay) throws InterruptedException {
        TimeUnit.SECONDS.sleep(delay);
        return "Process finished after " + delay + " seconds";
    }

    // Este endpoint nos servirá para simular ERRORES 500
    @GetMapping("/force-error")
    public String errorEndpoint() {
        throw new RuntimeException("Simulated Failure for SRE Monitoring");
    }
}
