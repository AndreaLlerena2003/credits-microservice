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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador CreditController que implementa la interfaz CreditsApi.
 *
 * - createTransaction: Registra una nueva transacción.
 * - creditsCreditIdGet: Obtiene un crédito por su ID.
 * - creditsCreditIdPut: Actualiza un crédito existente.
 * - creditsGet: Obtiene todos los créditos.
 * - creditsPost: Crea un nuevo crédito.
 * - deleteCredit: Elimina un crédito existente.
 * - getAllTransactions: Obtiene todas las transacciones.
 * - getTransactionsByCreditId: Obtiene transacciones por ID de crédito.
 */

@RestController
@RequiredArgsConstructor
public class CreditController implements CreditsApi {

    private final CreditOperationsPort creditOperationsPort;
    private final TransactionOperationsPort transactionOperationsPort;
    private static final Logger log = LoggerFactory.getLogger(CreditController.class);

    /**
     * POST /credits/transactions : Registrar una nueva transacción en la cuenta
     *
     * @param transaction (required)
     * @param exchange
     * @return Transacción registrada exitosamente (status code 201)
     */
    @Override
    public Mono<ResponseEntity<Transaction>> createTransaction(Mono<Transaction> transaction, ServerWebExchange exchange) {
        log.info("Iniciando creación de nueva transacción");
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
        log.info("Buscando crédito con ID: {}", creditId);
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
        log.info("Iniciando actualización de crédito con ID: {}", creditId);
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
        log.info("Obteniendo todos los créditos");
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
        log.info("Iniciando creación de nuevo crédito");
        return creditBase
                .flatMap(creditOperationsPort::createCredit)
                .map(credit -> ResponseEntity
                        .status(201)
                        .body(credit))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    /**
     * DELETE /credits/{creditId} : Elimina un crédito existente
     *
     * @param creditId ID del crédito a eliminar (required)
     * @param exchange
     * @return Crédito eliminado correctamente (status code 204)
     * or Crédito no encontrado (status code 404)
     * or Solicitud incorrecta (status code 400)
     * or Error interno del servidor (status code 500)
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteCredit(String creditId, ServerWebExchange exchange) {
        log.info("Iniciando eliminación de crédito con ID: {}", creditId);
        return creditOperationsPort.deleteCredit(creditId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> {
                    if (e.getMessage().contains("no encontrado")) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
    /**
     * GET /credits/transactions : Obtener todas las transacciones
     *
     * @param exchange
     * @return Historial completo de transacciones (status code 200)
     */
    @Override
    public Mono<ResponseEntity<Flux<Transaction>>> getAllTransactions(ServerWebExchange exchange) {
        log.info("Obteniendo todas las transacciones");
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
        log.info("Obteniendo transacciones para el crédito con ID: {}", creditId);

                Flux<Transaction> transactions = transactionOperationsPort.getTransactionByCreditId(creditId);
        return Mono.just(ResponseEntity.ok().body(transactions))
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Flux<Transaction>>build());
                });
    }


}
