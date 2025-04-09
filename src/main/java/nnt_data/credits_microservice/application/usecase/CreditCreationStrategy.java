package nnt_data.credits_microservice.application.usecase;

import nnt_data.credits_microservice.model.CreditBase;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Interfaz CreditCreationStrategy que define la estrategia para la creación de créditos.
 *
 * - createCredit: Método que crea un nuevo crédito basado en la implementación de la estrategia.
 *   - Recibe un objeto CreditBase como parámetro.
 *   - Devuelve un Mono<CreditBase> que representa el crédito creado de manera reactiva.
 *
 * Utiliza Mono de Reactor para manejar la operación de manera reactiva.
 */
@Component
public interface CreditCreationStrategy {
    Mono<CreditBase> createCredit(CreditBase credit);
}
