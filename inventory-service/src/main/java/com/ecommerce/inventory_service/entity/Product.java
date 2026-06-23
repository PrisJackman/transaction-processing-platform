package com.ecommerce.inventory_service.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "eco_product")
@Data
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    private String name;
    private Double price;
    private Integer stock;
}
