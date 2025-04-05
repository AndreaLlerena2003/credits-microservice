package nnt_data.credit_service.application.usecase.business;

import nnt_data.credit_service.application.usecase.UpdateCreationStrategy;
import nnt_data.credit_service.model.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Clase BusinessCreditUpdateStrategy que implementa la interfaz UpdateCreationStrategy.
 *
 * - updateCredit: Método que actualiza un crédito basado en el tipo de cliente y tipo de crédito.
 *   - Verifica que el tipo de cliente sea empresarial (CustomerType.BUSINESS).
 *   - Si el tipo de crédito es CreditType.CREDIT_CARD, convierte el crédito a CreditCard.
 *   - Si el tipo de crédito es CreditType.SIMPLE_CREDIT, convierte el crédito a SimpleCredit.
 *   - Devuelve el crédito actualizado como un Mono<CreditBase>.
 *
 * Utiliza Mono de Reactor para manejar la operación de manera reactiva.
 */
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
