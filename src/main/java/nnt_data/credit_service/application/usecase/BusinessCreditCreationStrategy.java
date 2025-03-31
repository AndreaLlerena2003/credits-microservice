package nnt_data.credit_service.application.usecase;

import nnt_data.credit_service.model.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BusinessCreditCreationStrategy implements CreditCreationStrategy {

    @Override
    public Mono<CreditBase> createCredit(CreditBase credit) {
        if (credit.getCustomerType() != CustomerType.BUSINESS) {
            return Mono.error(new IllegalArgumentException("Esta estrategia solo aplica para clientes empresariales"));
        }

        if (credit.getType() == CreditType.CREDIT_CARD) {
            CreditCard creditCard = (CreditCard) credit;
            if (creditCard.getAvailableCredit() == null) {
                creditCard.setAvailableCredit(credit.getAmount());
            }
            return Mono.just(creditCard);
        }

        if(credit.getType() == CreditType.SIMPLE_CREDIT) {
            SimpleCredit simpleCredit = (SimpleCredit) credit;
            if(simpleCredit.getAmountPaid() == null) {
                simpleCredit.setAmountPaid(0.0); // Cambiado a Double
            }
            return Mono.just(simpleCredit);
        }

        return Mono.just(credit);
    }
}
