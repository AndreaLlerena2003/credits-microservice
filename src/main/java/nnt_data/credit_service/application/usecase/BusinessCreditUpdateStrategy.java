package nnt_data.credit_service.application.usecase;

import nnt_data.credit_service.model.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BusinessCreditUpdateStrategy implements UpdateCreationStrategy {
    @Override
    public Mono<CreditBase> updateCredit(CreditBase credit) {

        if (credit.getCustomerType() != CustomerType.BUSINESS) {
            return Mono.error(new IllegalArgumentException("Esta estrategia solo aplica para clientes empresariales"));
        }

        if (credit.getType() == CreditType.CREDIT_CARD) {
            CreditCard creditCard = (CreditCard) credit;
            return Mono.just(creditCard);
        }

        if(credit.getType() == CreditType.SIMPLE_CREDIT) {
            SimpleCredit simpleCredit = (SimpleCredit) credit;
            return Mono.just(simpleCredit);
        }

        return Mono.just(credit);
    }
}
