package nnt_data.credits_microservice.application.port;

import nnt_data.credits_microservice.model.CreditBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * Interfaz CreditOperationsPort que define las operaciones de crédito en el servicio.
 *
 * - createCredit: Crea un nuevo crédito.
 * - updateCredit: Actualiza un crédito existente identificado por creditId.
 * - getByCreditId: Recupera un crédito específico por su creditId.
 * - getAllCredits: Recupera todos los créditos.
 * - deleteCredit: Elimina un crédito identificado por creditId.
 *
 * Utiliza Mono y Flux de Reactor para manejar las operaciones de manera reactiva.
 */
public interface CreditOperationsPort {
    Mono<CreditBase> createCredit(CreditBase credit);
    Mono<CreditBase> updateCredit(String creditId,CreditBase credit);
    Mono<CreditBase> getByCreditId(String creditId);
    Flux<CreditBase> getAllCredits();
    Mono<Void> deleteCredit(String creditId);
    Mono<Boolean> hasCreditCard(String customerId);
}
