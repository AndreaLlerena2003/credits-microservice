package nnt_data.credits_microservice.infrastructure.persistence.repository;

import nnt_data.credits_microservice.infrastructure.persistence.entity.CreditBaseEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * Repositorio CreditRepository para operaciones de persistencia de créditos.
 * - updateAmountPaidByCreditId: Actualiza el monto pagado de un crédito por su ID.
 * - updateAvailableAmountByCreditId: Actualiza el crédito disponible de un crédito por su ID.
 */
public interface CreditRepository extends ReactiveMongoRepository<CreditBaseEntity, String> {
    @Query("{ 'creditId': ?0 }")
    @Update("{ '$set': { 'amountPaid': ?1 } }")
    Mono<Void> updateAmountPaidByCreditId(String creditId, Double amountPaid);

    @Query("{ 'creditId': ?0 }")
    @Update("{ '$set': { 'availableCredit': ?1 } }")
    Mono<Void> updateAvilableAmountByCreditId(String creditId, Double availableAmount);

    Flux<CreditBaseEntity> findByCustomerId(String customerId);
}
