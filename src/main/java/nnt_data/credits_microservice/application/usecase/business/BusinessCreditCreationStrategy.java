package nnt_data.credits_microservice.application.usecase.business;

import nnt_data.credits_microservice.application.usecase.CreditCreationStrategy;
import nnt_data.credits_microservice.model.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Clase BusinessCreditCreationStrategy que implementa la estrategia de creación de créditos empresariales.
 *
 * - createCredit: Método que crea un crédito basado en el tipo de cliente y tipo de crédito.
 *   - Si el tipo de cliente no es empresarial, lanza una excepción.
 *   - Si el tipo de crédito es CREDIT_CARD, establece el crédito disponible si no está definido.
 *   - Si el tipo de crédito es SIMPLE_CREDIT, establece el monto pagado a 0.0 si no está definido.
 *
 * Utiliza Mono de Reactor para manejar las operaciones de manera reactiva.
 */
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
                simpleCredit.setAmountPaid(0.0);
            }
            return Mono.just(simpleCredit);
        }

        return Mono.just(credit);
    }
}
