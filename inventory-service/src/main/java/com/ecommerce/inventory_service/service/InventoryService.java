package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.consumer.OrderEventConsumer;
import com.ecommerce.inventory_service.dto.ProductResponseDTO;
import com.ecommerce.inventory_service.entity.Product;
import com.ecommerce.inventory_service.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public InventoryService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public boolean checkStock(Long id, Integer quantity){
        return productRepository.findById(id)
                .map(product -> {
                    boolean hashStock = product.getStock()>= quantity;
                    log.debug("Product {} stock: {}, has stock: {}", product.getName(), product.getStock(), hashStock);
                    return hashStock;
                }).orElse(false);
    }

    public ProductResponseDTO getProductDTO(Long id){
        Product product =  productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        return modelMapper.map(product,ProductResponseDTO.class);
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
}
