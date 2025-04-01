package nnt_data.credit_service.infrastructure.persistence.repository;

import nnt_data.credit_service.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
/**
 * Repositorio TransactionRepository para operaciones de persistencia de transacciones.
 *
 * - findByCreditId: Recupera todas las transacciones asociadas a un ID de crédito específico.
 */
public interface TransactionRepository extends ReactiveMongoRepository<TransactionEntity, String> {
    Flux<TransactionEntity> findByCreditId(String creditId);
}
