package nnt_data.credit_service.infrastructure.persistence.mapper;

import nnt_data.credit_service.infrastructure.persistence.entity.TransactionEntity;
import nnt_data.credit_service.model.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionMapper {
    Mono<TransactionEntity> toEntity(Transaction transaction);
    Mono<Transaction> toDomain(TransactionEntity transactionEntity);
}
