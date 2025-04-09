package nnt_data.credits_microservice.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import nnt_data.credits_microservice.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credits_microservice.model.CreditBase;
import nnt_data.credits_microservice.model.CreditCard;
import nnt_data.credits_microservice.model.SimpleCredit;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Implementación de CreditMapper para mapear entre CreditBase y CreditBaseEntity.
 *
 * - toEntity: Convierte CreditBase a CreditBaseEntity.
 * - toDomain: Convierte CreditBaseEntity a CreditBase.
 * - Métodos auxiliares para mapear campos comunes y específicos de tipo.
 */
@Component
@RequiredArgsConstructor
public class CreditMapperImpl implements CreditMapper {
    @Override
    public Mono<CreditBaseEntity> toEntity(CreditBase creditBase) {
        if (creditBase == null) {
            return Mono.error(new IllegalArgumentException("Cannot map null creditBase"));
        }
        return Mono.just(createCreditBaseEntity(creditBase));
    }

    @Override
    public Mono<CreditBase> toDomain(CreditBaseEntity creditBaseEntity) {
        if (creditBaseEntity == null) {
            return Mono.error(new IllegalArgumentException("Cannot map null customer"));
        }
        return Mono.defer(() -> {
            switch (creditBaseEntity.getType()) {
                case CREDIT_CARD:
                    return Mono.just(createCreditCard(creditBaseEntity));
                case SIMPLE_CREDIT:
                    return Mono.just(createSimpleCredit(creditBaseEntity));
                default:
                    return Mono.error(new IllegalArgumentException("Unknown customer type: " + creditBaseEntity.getType()));
            }
        });
    }

    private CreditBaseEntity createCreditBaseEntity(CreditBase creditBase) {
        CreditBaseEntity creditBaseEntity = new CreditBaseEntity();
        mapCommonFields(creditBase, creditBaseEntity);
        mapTypeSpecificFields(creditBase, creditBaseEntity);
        return creditBaseEntity;
    }

    private void mapCommonFields(CreditBase creditBase, CreditBaseEntity creditBaseEntity) {
        creditBaseEntity.setCreditId(creditBase.getCreditId());
        creditBaseEntity.setAmount(creditBase.getAmount());
        creditBaseEntity.setCustomerId(creditBase.getCustomerId());
        creditBaseEntity.setCustomerType(creditBase.getCustomerType());
        creditBaseEntity.setType(creditBase.getType());
    }

    private void mapCommonCreditBaseFields(CreditBaseEntity creditBaseEntity, CreditBase creditBase) {
       creditBase.setCreditId(creditBaseEntity.getCreditId());
       creditBase.setAmount(creditBaseEntity.getAmount());
       creditBase.setCustomerId(creditBaseEntity.getCustomerId());
       creditBase.setCustomerType(creditBaseEntity.getCustomerType());
       creditBase.setType(creditBase.getType());
    }

    private void mapTypeSpecificFields(CreditBase creditBase, CreditBaseEntity creditBaseEntity) {
        if (creditBase instanceof CreditCard creditCard) {
            creditBaseEntity.setCardNumber(creditCard.getCardNumber());
            creditBaseEntity.setAvailableCredit(creditCard.getAvailableCredit());
        } else if (creditBase instanceof SimpleCredit simpleCredit) {
            creditBaseEntity.setAmountPaid(simpleCredit.getAmountPaid());
        }
    }

    private CreditCard createCreditCard(CreditBaseEntity creditBaseEntity) {
        CreditCard creditCard = new CreditCard();
        mapCommonCreditBaseFields(creditBaseEntity, creditCard);
        creditCard.setCardNumber(creditBaseEntity.getCardNumber());
        creditCard.setAvailableCredit(creditBaseEntity.getAvailableCredit());
        return creditCard;
    }

    private SimpleCredit createSimpleCredit(CreditBaseEntity creditBaseEntity) {
        SimpleCredit simpleCredit = new SimpleCredit();
        mapCommonCreditBaseFields(creditBaseEntity, simpleCredit);
        simpleCredit.setAmountPaid(creditBaseEntity.getAmountPaid());
        return simpleCredit;
    }

}
