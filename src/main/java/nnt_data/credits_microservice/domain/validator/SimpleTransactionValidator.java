package nnt_data.credits_microservice.domain.validator;

import lombok.RequiredArgsConstructor;
import nnt_data.credits_microservice.infrastructure.persistence.mapper.CreditMapper;
import nnt_data.credits_microservice.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credits_microservice.model.CreditType;
import nnt_data.credits_microservice.model.SimpleCredit;
import nnt_data.credits_microservice.model.Transaction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Validador de transacciones simples.
 *
 * - Solo permite transacciones de tipo PAYMENT.
 * - Verifica que el crédito sea de tipo SIMPLE_CREDIT.
 * - Actualiza el monto pagado del crédito y valida que no exceda el monto total.
 */
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

                        return creditMapper.toEntity(credit)
                                .flatMap(creditEntity -> creditRepository.updateAmountPaidByCreditId(entity.getCreditId(), newAmountPaid)
                                        .thenReturn(entity))
                                .onErrorMap(e -> new IllegalArgumentException("Error al actualizar el crédito: " + e.getMessage()));
                    } catch (Exception e) {
                        return Mono.error(new IllegalArgumentException("Error en la validación: " + e.getMessage()));
                    }
                });
    }
}