package nnt_data.credit_service.domain.port;

import nnt_data.credit_service.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionOperationsPort {
    Mono<Transaction> createTransaction(Transaction transaction);
    Flux<Transaction> getTransactions();
    Flux<Transaction> getTransactionByCreditId(String creditId);
}
