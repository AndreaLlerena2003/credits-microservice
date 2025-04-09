package nnt_data.credits_microservice.domain.validator;

import reactor.core.publisher.Mono;
/**
 * Interfaz Validator que define un método para validar entidades de tipo T.
 *
 * - validate: Valida la entidad y devuelve un Mono<T> con el resultado de la validación.
 */
public interface Validator<T> {
    Mono<T> validate(T entity);
}
