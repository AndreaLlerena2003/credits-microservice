package nnt_data.credits_microservice.infrastructure.persistence.mapper;

import nnt_data.credits_microservice.infrastructure.persistence.entity.TransactionEntity;
import nnt_data.credits_microservice.model.Transaction;
import reactor.core.publisher.Mono;
/**
 * Interfaz TransactionMapper para mapear entre Transaction y TransactionEntity.
 *
 * - toEntity: Convierte una Transaction a TransactionEntity.
 * - toDomain: Convierte una TransactionEntity a Transaction.
 */
public interface TransactionMapper {
    Mono<TransactionEntity> toEntity(Transaction transaction);
    Mono<Transaction> toDomain(TransactionEntity transactionEntity);
}
