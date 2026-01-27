package com.company.franchise.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/*
 * Representa una franquicia completa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franchises")
public class Franchise {
    @Id
    private String id;
    private String name;
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
}
