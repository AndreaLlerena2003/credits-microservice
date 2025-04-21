package nnt_data.credits_microservice.infrastructure.persistence.repository;

import nnt_data.credits_microservice.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Date;

/**
 * Repositorio TransactionRepository para operaciones de persistencia de transacciones.
 *
 * - findByCreditId: Recupera todas las transacciones asociadas a un ID de crédito específico.
 */
public interface TransactionRepository extends ReactiveMongoRepository<TransactionEntity, String> {
    Flux<TransactionEntity> findByCreditId(String creditId);
    Flux<TransactionEntity> findByCreditIdAndDateBetween(
            String creditId,
            Date startDate,
            Date endDate
    );
}
