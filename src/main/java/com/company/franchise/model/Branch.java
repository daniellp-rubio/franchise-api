package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Representa una sucursal de una franquicia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    private String id;
    private String name;

    @Builder.Default
    private List<Product> products = new ArrayList<>();

    /*
     * Contructor que genera ID automático
     */
    public Branch(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.products = new ArrayList<>();
    }
}
