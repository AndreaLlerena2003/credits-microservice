package nnt_data.credit_service.domain.validator;

import lombok.RequiredArgsConstructor;
import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import org.springframework.stereotype.Component;

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
