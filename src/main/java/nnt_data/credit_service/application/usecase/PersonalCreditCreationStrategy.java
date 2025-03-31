package nnt_data.credit_service.application.usecase;

import nnt_data.credit_service.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credit_service.model.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PersonalCreditCreationStrategy implements CreditCreationStrategy {

    private final CreditRepository creditRepository;

    public PersonalCreditCreationStrategy(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    @Override
    public Mono<CreditBase> createCredit(CreditBase credit) {

        if (credit.getCustomerType() != CustomerType.PERSONAL) {
            return Mono.error(new IllegalArgumentException("Esta estrategia solo aplica para clientes personales"));
        }

        if (credit.getType() == CreditType.CREDIT_CARD) {
            CreditCard creditCard = (CreditCard) credit;
            if (creditCard.getAvailableCredit() == null) {
                creditCard.setAvailableCredit(credit.getAmount());
            }
            return Mono.just(creditCard);
        }

        if (credit.getType() == CreditType.SIMPLE_CREDIT) {
            SimpleCredit simpleCredit = (SimpleCredit) credit;
            if (simpleCredit.getAmountPaid() == null) {
                simpleCredit.setAmountPaid(0.0);
            }

            return creditRepository.findAll()
                    .filter(c -> c.getCustomerId().equals(credit.getCustomerId())
                            && c.getType() == CreditType.SIMPLE_CREDIT)
                    .hasElements()
                    .flatMap(hasCredits -> {
                        if (hasCredits) {
                            return Mono.error(new IllegalArgumentException(
                                    "Cliente personal ya tiene un crédito simple activo"));
                        }
                        return Mono.just(simpleCredit);
                    });
        }

        return Mono.error(new IllegalArgumentException("Tipo de crédito no soportado"));
    }
}