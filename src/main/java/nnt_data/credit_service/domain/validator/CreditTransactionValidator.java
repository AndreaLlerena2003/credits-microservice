package nnt_data.credit_service.domain.validator;

import lombok.RequiredArgsConstructor;
import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credit_service.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credit_service.model.Transaction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/**
 * Clase CreditTransactionValidator que implementa la validación de transacciones de crédito.
 *
 * - validate: Método que valida una transacción basada en el tipo de transacción y el crédito asociado.
 *   - Si el tipo de transacción es SPENT, valida que haya suficiente crédito disponible.
 *   - Si el tipo de transacción es PAYMENT, valida que el pago no exceda el límite del crédito.
 *   - Si el tipo de transacción no es válido, lanza una excepción.
 *   - Si el crédito no se encuentra, lanza una excepción.
 *
 * Métodos privados:
 * - validateSpentTransaction: Valida y actualiza el crédito disponible para una transacción de gasto.
 * - validatePaymentTransaction: Valida y actualiza el crédito disponible para una transacción de pago.
 *
 * Utiliza Mono de Reactor para manejar las operaciones de manera reactiva.
 */
@Component
@RequiredArgsConstructor
public class CreditTransactionValidator implements TransactionValidator{

    private final CreditRepository creditRepository;

    @Override
    public Mono<Transaction> validate(Transaction entity) {
        return creditRepository.findById(entity.getCreditId())
                .flatMap(credit -> {
                    if (Transaction.TypeEnum.SPENT.equals(entity.getType())) {
                        return validateSpentTransaction(entity, credit);
                    } else if (Transaction.TypeEnum.PAYMENT.equals(entity.getType())) {
                        return validatePaymentTransaction(entity, credit);
                    }
                    return Mono.error(new IllegalArgumentException("Tipo de transacción no válido"));
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Crédito no encontrado")));
    }

    private Mono<Transaction> validateSpentTransaction(Transaction transaction, CreditBaseEntity credit) {
        double newAvailableCredit = credit.getAvailableCredit() - transaction.getAmount();

        if (newAvailableCredit < 0) {
            return Mono.error(new IllegalArgumentException("Crédito disponible insuficiente"));
        }
        return creditRepository.updateAvilableAmountByCreditId(credit.getCreditId(), newAvailableCredit)
                .thenReturn(transaction);
    }

    private Mono<Transaction> validatePaymentTransaction(Transaction transaction, CreditBaseEntity credit) {
        double newAvailableCredit = credit.getAvailableCredit() + transaction.getAmount();

        if (newAvailableCredit > credit.getAmount()) {
            return Mono.error(new IllegalArgumentException("El pago excede el límite del crédito"));
        }
        credit.setAvailableCredit(newAvailableCredit);
        return creditRepository.save(credit)
                .thenReturn(transaction);
    }
}
