package nnt_data.credit_service.application.usecase;

import nnt_data.credit_service.model.CreditBase;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public interface UpdateCreationStrategy {
    Mono<CreditBase> updateCredit(CreditBase credit);
}
