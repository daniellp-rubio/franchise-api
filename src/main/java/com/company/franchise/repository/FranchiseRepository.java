package com.company.franchise.repository;

import com.company.franchise.model.Franchise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/*
 * Repositorio reactivo para operaciones con franquicias en MongoDB
 */
@Repository
public interface FranchiseRepository extends ReactiveMongoRepository<Franchise, String> {

}
