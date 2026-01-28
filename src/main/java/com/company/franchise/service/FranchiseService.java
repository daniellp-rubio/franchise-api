package com.company.franchise.service;

import com.company.franchise.dto.RequestDTO;
import com.company.franchise.dto.ResponseDTO;
import com.company.franchise.exception.CustomExceptions;
import com.company.franchise.model.Branch;
import com.company.franchise.model.Franchise;
import com.company.franchise.model.Product;
import com.company.franchise.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/*
 * Servicio con lógica de negocio para gestión de franquicias
 * Implementación reactiva con Project Reactor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FranchiseService {
    private final FranchiseRepository franchiseRepository;

    /**
     * Crea una nueva franquicia
     *
     * @param request DTO con el nombre de la franquicia
     * @return Mono con la franquicia creada
     */
    public Mono<ResponseDTO.FranchiseResponse> createFranchise(RequestDTO.CreateFranchiseRequest request) {
        log.info("Creando nueva franquicia: {}", request.getName());

        Franchise franchise = Franchise.builder()
                .name(request.getName())
                .build();

        return franchiseRepository.save(franchise)
                .map(ResponseDTO.FranchiseResponse::fromEntity)
                .doOnSuccess(f -> log.info("Franquicia creada con ID: {}", f.getId()));
    }

    /**
     * Agrega una nueva sucursal a una franquicia existente
     *
     * @param franchiseId ID de la franquicia
     * @param request DTO con el nombre de la sucursal
     * @return Mono con la franquicia actualizada
     */
    public Mono<ResponseDTO.FranchiseResponse> addBranchToFranchise( String franchiseId, RequestDTO.CreateBranchRequest request) {
        log.info("Agregando surcursal '{}' a franquicia {}", request.getName(), franchiseId);

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    boolean branchExists = franchise.getBranches().stream()
                            .anyMatch(b -> b.getName().equalsIgnoreCase(request.getName()));

                    if (branchExists) {
                        return Mono.error(new CustomExceptions.DuplicateNameException(
                                "Sucursal", request.getName()));
                    }

                    Branch newBranch = new Branch(request.getName());
                    franchise.getBranches().add(newBranch);

                    return franchiseRepository.save(franchise);
                })
                .map(ResponseDTO.FranchiseResponse::fromEntity)
                .doOnSuccess(f -> log.info("Surcursal agregada exitosamente"));
    }

    /**
     * Agrega un nuevo producto a una sucursal
     *
     * @param franchiseId ID de la franquicia
     * @param branchId ID de la sucursal
     * @param request DTO con nombre y stock del producto
     * @return Mono con la franquicia actualizada
     */
    public Mono<ResponseDTO.FranchiseResponse> addProductToBranch(String franchiseId, String branchId, RequestDTO.CreateProductRequest request) {
        log.info("Agregnado producto '{}' a surcursal {} de franquicia {}",
                request.getName(), branchId, franchiseId);

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = findBranchById(franchise, branchId);

                    boolean productExists = branch.getProducts().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()));

                    if (productExists) {
                        return Mono.error(new CustomExceptions.DuplicateNameException(
                                "producto", request.getName()));
                    }

                    Product newProduct = new Product(request.getName(), request.getStock());
                    branch.getProducts().add(newProduct);

                    return franchiseRepository.save(franchise);
                })
                .map(ResponseDTO.FranchiseResponse::fromEntity)
                .doOnSuccess(f -> log.info("Producto agregado exitosamente"));
    }

    /**
     * Elimina un producto de una sucursal
     *
     * @param franchiseId ID de la franquicia
     * @param branchId ID de la sucursal
     * @param productId ID del producto a eliminar
     * @return Mono con la franquicia actualizada
     */
    public Mono<ResponseDTO.FranchiseResponse> deleteProductFromBranch(String franchiseId, String branchId, String productId) {
        log.info("Eliminando producto {} de sucursal {} de franquicia {}",
                productId, branchId, franchiseId);

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = findBranchById(franchise, branchId);

                    // Intentar eliminar el producto
                    boolean removed = branch.getProducts()
                            .removeIf(p -> p.getId().equals(productId));

                    if (!removed) {
                        return Mono.error(new CustomExceptions.ProductNotFoundException(productId));
                    }

                    return franchiseRepository.save(franchise);
                })
                .map(ResponseDTO.FranchiseResponse::fromEntity)
                .doOnSuccess(f -> log.info("Producto eliminado exitosamente"));
    }

    /**
     * Actualiza el stock de un producto
     *
     * @param franchiseId ID de la franquicia
     * @param branchId ID de la sucursal
     * @param productId ID del producto
     * @param request DTO con el nuevo stock
     * @return Mono con la franquicia actualizada
     */
    public Mono<ResponseDTO.FranchiseResponse> updateProductStock(String franchiseId, String branchId, String productId, RequestDTO.UpdateStockRequest request) {

        log.info("Actualizando stock del producto {} a {}", productId, request.getStock());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = findBranchById(franchise, branchId);
                    Product product = findProductById(branch, productId);

                    // Actualizar el stock
                    product.setStock(request.getStock());

                    return franchiseRepository.save(franchise);
                })
                .map(ResponseDTO.FranchiseResponse::fromEntity)
                .doOnSuccess(f -> log.info("Stock actualizado exitosamente"));
    }

    /**
     * Obtiene el producto con mayor stock de cada sucursal de una franquicia
     * Este es el requisito más complejo
     *
     * @param franchiseId ID de la franquicia
     * @return Flux con los productos de mayor stock por sucursal
     */
    public Flux<ResponseDTO.TopStockProductResponse> getTopStockProductsByFranchise(String franchiseId) {

        log.info("Obteniendo productos con mayor stock para franquicia {}", franchiseId);

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches()))
                .flatMap(branch -> {
                    // Si la sucursal no tiene productos, saltarla
                    if (branch.getProducts().isEmpty()) {
                        return Mono.empty();
                    }

                    // Encontrar el producto con mayor stock en esta sucursal
                    Product topProduct = branch.getProducts().stream()
                            .max(Comparator.comparing(Product::getStock))
                            .orElse(null);

                    if (topProduct == null) {
                        return Mono.empty();
                    }

                    // Construir el DTO de respuesta
                    return Mono.just(ResponseDTO.TopStockProductResponse.builder()
                            .branchId(branch.getId())
                            .branchName(branch.getName())
                            .productId(topProduct.getId())
                            .productName(topProduct.getName())
                            .stock(topProduct.getStock())
                            .build());
                });
    }

    /**
     * Actualiza el nombre de una franquicia
     */
    public Mono<ResponseDTO.FranchiseResponse> updateFranchiseName(String franchiseId, RequestDTO.UpdateNameRequest request) {
        log.info("Actualizando nombre de franquicia {} a '{}'", franchiseId, request.getName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    franchise.setName(request.getName());
                    return franchiseRepository.save(franchise);
                })
                .map(ResponseDTO.FranchiseResponse::fromEntity)
                .doOnSuccess(f -> log.info("Nombre de franquicia actualizado exitosamente"));
    }

    /**
     * Actualiza el nombre de una sucursal
     */
    public Mono<ResponseDTO.FranchiseResponse> updateBranchName( String franchiseId, String branchId, RequestDTO.UpdateNameRequest request) {
        log.info("Actualizando nombre de sucursal {} a '{}'", branchId, request.getName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = findBranchById(franchise, branchId);
                    branch.setName(request.getName());
                    return franchiseRepository.save(franchise);
                })
                .map(ResponseDTO.FranchiseResponse::fromEntity)
                .doOnSuccess(f -> log.info("Nombre de sucursal actualizado exitosamente"));
    }

    /**
     * Actualiza el nombre de un producto
     */
    public Mono<ResponseDTO.FranchiseResponse> updateProductName( String franchiseId, String branchId, String productId, RequestDTO.UpdateNameRequest request) {
        log.info("Actualizando nombre de producto {} a '{}'", productId, request.getName());

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = findBranchById(franchise, branchId);
                    Product product = findProductById(branch, productId);
                    product.setName(request.getName());
                    return franchiseRepository.save(franchise);
                })
                .map(ResponseDTO.FranchiseResponse::fromEntity)
                .doOnSuccess(f -> log.info("Nombre de producto actualizado exitosamente"));
    }

    /**
     * Obtiene todas las franquicias
     */
    public Flux<ResponseDTO.FranchiseResponse> getAllFranchises() {
        log.info("Obteniendo todas las franquicias");
        return franchiseRepository.findAll()
                .map(ResponseDTO.FranchiseResponse::fromEntity);
    }

    /**
     * Obtiene una franquicia por ID
     */
    public Mono<ResponseDTO.FranchiseResponse> getFranchiseById(String franchiseId) {
        log.info("Obteniendo franquicia {}", franchiseId);
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomExceptions.FranchiseNotFoundException(franchiseId)))
                .map(ResponseDTO.FranchiseResponse::fromEntity);
    }

    /**
     * Busca una sucursal por ID dentro de una franquicia
     * Lanza excepción si no existe
     */
    private Branch findBranchById(Franchise franchise, String branchId) {
        return franchise.getBranches().stream()
                .filter(b -> b.getId().equals(branchId))
                .findFirst()
                .orElseThrow(() -> new CustomExceptions.BranchNotFoundException(branchId));
    }

    /**
     * Busca un producto por ID dentro de una sucursal
     * Lanza excepción si no existe
     */
    private Product findProductById(Branch branch, String productId) {
        return branch.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CustomExceptions.ProductNotFoundException(productId));
    }

}
