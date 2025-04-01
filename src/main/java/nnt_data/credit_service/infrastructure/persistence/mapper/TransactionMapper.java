package nnt_data.credit_service.infrastructure.persistence.mapper;

import nnt_data.credit_service.infrastructure.persistence.entity.TransactionEntity;
import nnt_data.credit_service.model.Transaction;
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
