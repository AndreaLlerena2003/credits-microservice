package nnt_data.credit_service.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import nnt_data.credit_service.api.CreditsApi;
import nnt_data.credit_service.application.port.CreditOperationsPort;
import nnt_data.credit_service.application.port.TransactionOperationsPort;
import nnt_data.credit_service.model.CreditBase;
import nnt_data.credit_service.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RestController
@RequiredArgsConstructor
public class CreditController implements CreditsApi {

    private final CreditOperationsPort creditOperationsPort;
    private final TransactionOperationsPort transactionOperationsPort;

    /**
     * POST /credits/transactions : Registrar una nueva transacción en la cuenta
     *
     * @param transaction (required)
     * @param exchange
     * @return Transacción registrada exitosamente (status code 201)
     */
    @Override
    public Mono<ResponseEntity<Transaction>> createTransaction(Mono<Transaction> transaction, ServerWebExchange exchange) {
        return transaction
                .flatMap(transactionOperationsPort::createTransaction)
                .map(result -> ResponseEntity
                        .status(201)
                        .body(result));
    }

    /**
     * GET /credits/{creditId} : Obtiene un crédito por su ID
     *
     * @param creditId (required)
     * @param exchange
     * @return Crédito obtenido correctamente (status code 200)
     */
    @Override
    public Mono<ResponseEntity<CreditBase>> creditsCreditIdGet(String creditId, ServerWebExchange exchange) {
        return creditOperationsPort.getByCreditId(creditId)
                .map(credit -> ResponseEntity.ok().body(credit))
                .onErrorResume(IllegalArgumentException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * PUT /credits/{creditId} : Actualiza un crédito existente
     *
     * @param creditId   (required)
     * @param creditBase (optional)
     * @param exchange
     * @return Crédito actualizado correctamente (status code 200)
     */
    @Override
    public Mono<ResponseEntity<CreditBase>> creditsCreditIdPut(String creditId, Mono<CreditBase> creditBase, ServerWebExchange exchange) {
        return creditBase
                .map(credit -> {
                    credit.setCreditId(creditId);
                    return credit;
                })
                .flatMap(credit -> creditOperationsPort.updateCredit(creditId,credit))
                .map(credit -> ResponseEntity
                        .ok()
                        .body(credit))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    /**
     * GET /credits : Obtiene todos los créditos
     *
     * @param exchange
     * @return Lista de créditos obtenida correctamente (status code 200)
     */
    @Override
    public Mono<ResponseEntity<Flux<CreditBase>>> creditsGet(ServerWebExchange exchange) {
        Flux<CreditBase> credits = creditOperationsPort.getAllCredits();
        return Mono.just(ResponseEntity.ok().body(credits));
    }

    /**
     * POST /credits : Crea un nuevo crédito
     *
     * @param creditBase (optional)
     * @param exchange
     * @return Crédito creado correctamente (status code 201)
     */
    @Override
    public Mono<ResponseEntity<CreditBase>> creditsPost(Mono<CreditBase> creditBase, ServerWebExchange exchange) {
        return creditBase
                .flatMap(creditOperationsPort::createCredit)
                .map(credit -> ResponseEntity
                        .status(201)
                        .body(credit))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    /**
     * GET /credits/transactions : Obtener todas las transacciones
     *
     * @param exchange
     * @return Historial completo de transacciones (status code 200)
     */
    @Override
    public Mono<ResponseEntity<Flux<Transaction>>> getAllTransactions(ServerWebExchange exchange) {
        Flux<Transaction> transactions = transactionOperationsPort.getTransactions();
        return Mono.just(ResponseEntity.ok().body(transactions));
    }

    /**
     * GET /credits/{creditId}/transactions : Obtener transacciones por ID de crédito
     * Recupera todas las transacciones asociadas a un crédito específico
     *
     * @param creditId ID del crédito del cual se desean obtener las transacciones (required)
     * @param exchange
     * @return Lista de transacciones obtenida exitosamente (status code 200)
     * or Crédito no encontrado (status code 404)
     * or Solicitud incorrecta (status code 400)
     * or Error interno del servidor (status code 500)
     */
    @Override
    public Mono<ResponseEntity<Flux<Transaction>>> getTransactionsByCreditId(String creditId, ServerWebExchange exchange) {
        Flux<Transaction> transactions = transactionOperationsPort.getTransactionByCreditId(creditId);
        return Mono.just(ResponseEntity.ok().body(transactions))
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Flux<Transaction>>build());
                });
    }


}
