package nnt_data.credits_microservice.domain.validator;

import lombok.RequiredArgsConstructor;
import nnt_data.credits_microservice.infrastructure.persistence.entity.CreditBaseEntity;
import org.springframework.stereotype.Component;
/**
 * Clase ValidatorFactory que proporciona validadores de transacciones basados en el tipo de crédito.
 *
 * - getTransactionValidator: Método que devuelve el validador adecuado según el tipo de crédito.
 *   - Si el tipo de crédito es CREDIT_CARD, devuelve CreditTransactionValidator.
 *   - Si el tipo de crédito es SIMPLE_CREDIT, devuelve SimpleTransactionValidator.
 *   - Si el tipo de crédito no es soportado, lanza una excepción.
 *
 * Utiliza los validadores SimpleTransactionValidator y CreditTransactionValidator inyectados mediante constructor.
 */
@Component
@RequiredArgsConstructor
public class ValidatorFactory {

    private final SimpleTransactionValidator simpleTransactionValidator;
    private final CreditTransactionValidator creditTransactionValidator;

    public TransactionValidator getTransactionValidator(CreditBaseEntity creditBaseEntity) {
        switch (creditBaseEntity.getType()){
            case CREDIT_CARD:
                return creditTransactionValidator;
            case SIMPLE_CREDIT:
                return simpleTransactionValidator;
            default:
                throw new IllegalArgumentException("Tipo de cuenta no soportado: " + creditBaseEntity.getType());
        }

    }

}
