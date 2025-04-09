package nnt_data.credits_microservice.application.usecase;

import nnt_data.credits_microservice.model.CreditBase;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Interfaz UpdateCreationStrategy que define la estrategia para la actualización de créditos.
 *
 * - updateCredit: Método que actualiza un crédito basado en la implementación de la estrategia.
 *   - Recibe un objeto CreditBase como parámetro.
 *   - Devuelve un Mono<CreditBase> que representa el crédito actualizado de manera reactiva.
 *
 * Utiliza Mono de Reactor para manejar la operación de manera reactiva.
 */
@Component
public interface UpdateCreationStrategy {
    Mono<CreditBase> updateCredit(CreditBase credit);
}
