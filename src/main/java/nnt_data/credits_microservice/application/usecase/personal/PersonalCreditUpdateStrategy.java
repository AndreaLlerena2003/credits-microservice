package nnt_data.credits_microservice.application.usecase.personal;

import nnt_data.credits_microservice.application.usecase.UpdateCreationStrategy;
import nnt_data.credits_microservice.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credits_microservice.model.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Clase PersonalCreditUpdateStrategy que implementa la estrategia de actualización de créditos personales.
 *
 * - updateCredit: Método que actualiza un crédito basado en el tipo de cliente y tipo de crédito.
 *   - Si el tipo de cliente no es personal, lanza una excepción.
 *   - Si el tipo de crédito es CREDIT_CARD, establece el crédito disponible si no está definido.
 *   - Si el tipo de crédito es SIMPLE_CREDIT, devuelve el crédito simple tal como está.
 *   - Si el tipo de crédito no es soportado para clientes personales, lanza una excepción.
 *
 * Utiliza Mono de Reactor para manejar las operaciones de manera reactiva.
 */
@Component
public class PersonalCreditUpdateStrategy implements UpdateCreationStrategy {

    private final CreditRepository creditRepository;

    public PersonalCreditUpdateStrategy(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    @Override
    public Mono<CreditBase> updateCredit(CreditBase credit) {
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
            return Mono.just(simpleCredit);
        }
        return Mono.error(new IllegalArgumentException("Tipo de crédito no soportado para clientes personales"));
    }
}