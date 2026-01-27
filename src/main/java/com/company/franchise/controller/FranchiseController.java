package com.company.franchise.controller;

import com.company.franchise.dto.RequestDTO;
import com.company.franchise.dto.ResponseDTO;
import com.company.franchise.service.FranchiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para gestión de franquicias
 * Todos los endpoints son reactivos y retornan Mono o Flux
 *
 * Base URL: /api/franchises
 */
@RestController
@RequestMapping("/api/franchises")
@RequiredArgsConstructor
@Tag(name = "Franchises", description = "API para gestión de franquicias, sucursales y productos")
public class FranchiseController {
    // Inyección del servicio
    private final FranchiseService franchiseService;

    /**
     * POST /api/franchises
     * Body: {"name": "Starbucks"}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nueva franquicia",
            description = "Endpoint para agregar una nueva franquicia al sistema")
    public Mono<ResponseDTO.FranchiseResponse> createFranchise(
            @Valid @RequestBody RequestDTO.CreateFranchiseRequest request) {
        return franchiseService.createFranchise(request);
    }

    /**
     * POST /api/franchises/{franchiseId}/branches
     * Body: {"name": "Sucursal Centro"}
     */
    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar sucursal a franquicia",
            description = "Endpoint para agregar una nueva sucursal a una franquicia existente")
    public Mono<ResponseDTO.FranchiseResponse> addBranchToFranchise(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId,
            @Valid @RequestBody RequestDTO.CreateBranchRequest request) {
        return franchiseService.addBranchToFranchise(franchiseId, request);
    }

    /**
     * POST /api/franchises/{franchiseId}/branches/{branchId}/products
     * Body: {"name": "Café Latte", "stock": 50}
     */
    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar producto a sucursal",
            description = "Endpoint para agregar un nuevo producto a una sucursal")
    public Mono<ResponseDTO.FranchiseResponse> addProductToBranch(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal")
            @PathVariable String branchId,
            @Valid @RequestBody RequestDTO.CreateProductRequest request) {
        return franchiseService.addProductToBranch(franchiseId, branchId, request);
    }

    /**
     * DELETE /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}
     */
    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @Operation(summary = "Eliminar producto de sucursal",
            description = "Endpoint para eliminar un producto de una sucursal")
    public Mono<ResponseDTO.FranchiseResponse> deleteProductFromBranch(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal")
            @PathVariable String branchId,
            @Parameter(description = "ID del producto")
            @PathVariable String productId) {
        return franchiseService.deleteProductFromBranch(franchiseId, branchId, productId);
    }

    /**
     * PATCH /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock
     * Body: {"stock": 100}
     */
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    @Operation(summary = "Modificar stock de producto",
            description = "Endpoint para actualizar la cantidad de stock de un producto")
    public Mono<ResponseDTO.FranchiseResponse> updateProductStock(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal")
            @PathVariable String branchId,
            @Parameter(description = "ID del producto")
            @PathVariable String productId,
            @Valid @RequestBody RequestDTO.UpdateStockRequest request) {
        return franchiseService.updateProductStock(franchiseId, branchId, productId, request);
    }

    /**
     * GET /api/franchises/{franchiseId}/top-stock-products
     *
     * Retorna el producto con mayor stock de cada sucursal
     * Response: [
     *   {
     *     "branchId": "uuid",
     *     "branchName": "Sucursal Centro",
     *     "productId": "uuid",
     *     "productName": "Café Latte",
     *     "stock": 150
     *   }
     * ]
     */
    @GetMapping("/{franchiseId}/top-stock-products")
    @Operation(summary = "Productos con mayor stock por sucursal",
            description = "Endpoint que retorna el producto con mayor stock de cada sucursal para una franquicia específica")
    public Flux<ResponseDTO.TopStockProductResponse> getTopStockProductsByFranchise(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId) {
        return franchiseService.getTopStockProductsByFranchise(franchiseId);
    }

    /**
     * PATCH /api/franchises/{franchiseId}/name
     * Body: {"name": "Nuevo Nombre"}
     */
    @PatchMapping("/{franchiseId}/name")
    @Operation(summary = "Actualizar nombre de franquicia",
            description = "Endpoint para actualizar el nombre de una franquicia")
    public Mono<ResponseDTO.FranchiseResponse> updateFranchiseName(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId,
            @Valid @RequestBody RequestDTO.UpdateNameRequest request) {
        return franchiseService.updateFranchiseName(franchiseId, request);
    }

    /**
     * PATCH /api/franchises/{franchiseId}/branches/{branchId}/name
     * Body: {"name": "Nuevo Nombre"}
     */
    @PatchMapping("/{franchiseId}/branches/{branchId}/name")
    @Operation(summary = "Actualizar nombre de sucursal",
            description = "Endpoint para actualizar el nombre de una sucursal")
    public Mono<ResponseDTO.FranchiseResponse> updateBranchName(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal")
            @PathVariable String branchId,
            @Valid @RequestBody RequestDTO.UpdateNameRequest request) {
        return franchiseService.updateBranchName(franchiseId, branchId, request);
    }

    /**
     * PATCH /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name
     * Body: {"name": "Nuevo Nombre"}
     */
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/name")
    @Operation(summary = "Actualizar nombre de producto",
            description = "Endpoint para actualizar el nombre de un producto")
    public Mono<ResponseDTO.FranchiseResponse> updateProductName(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId,
            @Parameter(description = "ID de la sucursal")
            @PathVariable String branchId,
            @Parameter(description = "ID del producto")
            @PathVariable String productId,
            @Valid @RequestBody RequestDTO.UpdateNameRequest request) {
        return franchiseService.updateProductName(franchiseId, branchId, productId, request);
    }

    /**
     * GET /api/franchises
     * Listar todas las franquicias
     */
    @GetMapping
    @Operation(summary = "Listar todas las franquicias",
            description = "Endpoint para obtener la lista completa de franquicias")
    public Flux<ResponseDTO.FranchiseResponse> getAllFranchises() {
        return franchiseService.getAllFranchises();
    }

    /**
     * GET /api/franchises/{franchiseId}
     * Obtener una franquicia específica
     */
    @GetMapping("/{franchiseId}")
    @Operation(summary = "Obtener franquicia por ID",
            description = "Endpoint para obtener los detalles de una franquicia específica")
    public Mono<ResponseDTO.FranchiseResponse> getFranchiseById(
            @Parameter(description = "ID de la franquicia")
            @PathVariable String franchiseId) {
        return franchiseService.getFranchiseById(franchiseId);
    }
}
