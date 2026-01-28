package com.company.franchise.dto;

import com.company.franchise.model.Branch;
import com.company.franchise.model.Franchise;
import com.company.franchise.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * DTOs para las respuestas de la API al cliente
 */
public class ResponseDTO {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FranchiseResponse {
        private String id;
        private String name;
        private List<BranchResponse> branches;

        public static FranchiseResponse fromEntity(Franchise franchise) {
            return FranchiseResponse.builder()
                    .id(franchise.getId())
                    .name(franchise.getName())
                    .branches(franchise.getBranches().stream()
                            .map(BranchResponse::fromEntity)
                            .toList())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchResponse {
        private String id;
        private String name;
        private List<ProductResponse> products;

        public static BranchResponse fromEntity(Branch branch) {
            return BranchResponse.builder()
                    .id(branch.getId())
                    .name(branch.getName())
                    .products(branch.getProducts().stream()
                            .map(ProductResponse::fromEntity)
                            .toList())
                    .build();

        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private String id;
        private String name;
        private Integer stock;

        public static ProductResponse fromEntity(Product product) {
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .stock(product.getStock())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopStockProductResponse {
        private String branchId;
        private String branchName;
        private String productId;
        private String productName;
        private Integer stock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
        private String error;
        private Integer status;
    }
}
