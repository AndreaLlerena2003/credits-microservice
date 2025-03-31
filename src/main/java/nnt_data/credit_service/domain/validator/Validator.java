package nnt_data.credit_service.domain.validator;

import reactor.core.publisher.Mono;

public interface Validator<T> {
    Mono<T> validate(T entity);
}
