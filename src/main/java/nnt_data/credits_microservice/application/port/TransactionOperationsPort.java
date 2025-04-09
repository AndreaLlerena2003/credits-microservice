package nnt_data.credits_microservice.application.port;

import nnt_data.credits_microservice.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * Interfaz TransactionOperationsPort que define las operaciones de transacciones en el servicio de crédito.
 *
 * - createTransaction: Crea una nueva transacción.
 * - getTransactions: Recupera todas las transacciones.
 * - getTransactionByCreditId: Recupera todas las transacciones asociadas a un crédito específico identificado por creditId.
 *
 * Utiliza Mono y Flux de Reactor para manejar las operaciones de manera reactiva.
 */
public interface TransactionOperationsPort {
    Mono<Transaction> createTransaction(Transaction transaction);
    Flux<Transaction> getTransactions();
    Flux<Transaction> getTransactionByCreditId(String creditId);
}
