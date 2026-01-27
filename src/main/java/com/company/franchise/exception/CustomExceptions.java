package com.company.franchise.exception;

/*
 * Exepciones personalizadas para la API
 * Cada excepción representa un error específico del dominio
 */
public class CustomExceptions {
    public static class FranchiseNotFoundException extends RuntimeException {
        public FranchiseNotFoundException(String franchiseId) {
            super("Franquicia con ID" + franchiseId + " no encontrada");
        }
    }

    public static class BranchNotFoundException extends RuntimeException {
        public BranchNotFoundException(String branchId) {
            super("Surcursal con ID" + branchId + " no encontrada");
        }
    }

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String productId) {
            super("Producto con ID" + productId + " no encontrada");
        }
    }

    public static class DuplicateNameException extends RuntimeException {
        public DuplicateNameException(String entityType, String name) {
            super(entityType + "con nombre '" + name + "' ya existe");
        }
    }
}
