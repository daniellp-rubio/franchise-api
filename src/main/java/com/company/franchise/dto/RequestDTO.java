package com.company.franchise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/*
 * DTOs para las peticiones del cliente a la API
 */
public class RequestDTO {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateFranchiseRequest {
        @NotBlank(message = "El nombre de la franquicia es requerido")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBranchRequest {
        @NotBlank(message = "El nombre de la franquicia es requerido")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @NotBlank(message = "El nombre de la franquicia es requerido")
        private String name;

        @NotNull(message = "El stock es requerido")
        @Positive(message = "El stock debe ser mayor a 0")
        private Integer stock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStockRequest {
        @NotNull(message = "El stock es requerido")
        @Positive(message = "El stock debe ser mayor a 0")
        private Integer stock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateNameRequest {
        @NotBlank(message = "El nombre es requerido")
        private String name;
    }
}
