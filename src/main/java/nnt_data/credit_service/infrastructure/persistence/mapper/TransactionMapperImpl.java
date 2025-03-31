package nnt_data.credit_service.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import nnt_data.credit_service.infrastructure.persistence.model.TransactionEntity;
import nnt_data.credit_service.model.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
