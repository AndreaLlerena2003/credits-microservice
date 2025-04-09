package nnt_data.credits_microservice.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import nnt_data.credits_microservice.infrastructure.persistence.entity.TransactionEntity;
import nnt_data.credits_microservice.model.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Implementación de TransactionMapper para mapear entre Transaction y TransactionEntity.
 *
 * - toEntity: Convierte una Transaction a TransactionEntity utilizando BeanUtils.
 * - toDomain: Convierte una TransactionEntity a Transaction utilizando BeanUtils.
 */
@Component
@RequiredArgsConstructor
public class TransactionMapperImpl implements TransactionMapper{

    @Override
    public Mono<TransactionEntity> toEntity(Transaction transaction) {
        TransactionEntity transactionEntity = new TransactionEntity();
        BeanUtils.copyProperties(transaction, transactionEntity);
        return Mono.just(transactionEntity);
    }

    @Override
    public Mono<Transaction> toDomain(TransactionEntity transactionEntity) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(transactionEntity, transaction);
        return Mono.just(transaction);
    }
}
