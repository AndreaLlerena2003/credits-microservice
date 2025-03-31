package nnt_data.credit_service.domain.validator;

import lombok.RequiredArgsConstructor;
import nnt_data.credit_service.infrastructure.persistence.mapper.CreditMapper;
import nnt_data.credit_service.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credit_service.infrastructure.persistence.repository.TransactionRepository;
import nnt_data.credit_service.model.CreditType;
import nnt_data.credit_service.model.SimpleCredit;
import nnt_data.credit_service.model.Transaction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SimpleTransactionValidator implements TransactionValidator {

    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    @Override
    public Mono<Transaction> validate(Transaction entity) {
        if (!Transaction.TypeEnum.PAYMENT.equals(entity.getType())) {
            return Mono.error(new IllegalArgumentException("Solo se permiten transacciones de tipo payment para un credito simple"));
        }
        return creditRepository.findById(entity.getCreditId())
                .filter(credit -> credit.getType() == CreditType.SIMPLE_CREDIT)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Crédito no encontrado o no es de tipo simple")))
                .flatMap(creditMapper::toDomain)
                .cast(SimpleCredit.class)
                .flatMap(credit -> {
                    try {
                        double newAmountPaid = (credit.getAmountPaid() != null ? credit.getAmountPaid() : 0.0)
                                + entity.getAmount();

                        if (newAmountPaid > credit.getAmount()) {
                            return Mono.error(new IllegalArgumentException("El pago excede el monto total del crédito"));
                        }

                        if (Double.compare(credit.getAmountPaid(), credit.getAmount()) == 0) {
                            return Mono.error(new IllegalArgumentException("El crédito ya está pagado en su totalidad"));
                        }

                        credit.setAmountPaid(newAmountPaid);
                        return creditMapper.toEntity(credit)
                                .flatMap(creditRepository::save)
                                .thenReturn(entity)
                                .onErrorMap(e -> new IllegalArgumentException("Error al actualizar el crédito: " + e.getMessage()));
                    } catch (Exception e) {
                        return Mono.error(new IllegalArgumentException("Error en la validación: " + e.getMessage()));
                    }
                });
    }
}