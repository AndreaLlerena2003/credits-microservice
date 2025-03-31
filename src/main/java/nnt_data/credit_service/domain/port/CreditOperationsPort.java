package nnt_data.credit_service.domain.port;

import nnt_data.credit_service.model.CreditBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditOperationsPort {
    Mono<CreditBase> createCredit(CreditBase credit);
    Mono<CreditBase> updateCredit(String creditId,CreditBase credit);
    Mono<CreditBase> getByCreditId(String creditId);
    Flux<CreditBase> getAllCredits();
}
