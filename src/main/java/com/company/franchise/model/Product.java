package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Reprensenta un producto en una surcursal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private Integer stock;

    public Product(String name, Integer stock) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.stock = stock;
    }
}

