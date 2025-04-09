package nnt_data.credit_service.domain.validator;


import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credit_service.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credit_service.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditTransactionValidatorTest {

    @Mock
    private CreditRepository creditRepository;

    private CreditTransactionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreditTransactionValidator(creditRepository);
    }

    @Test
    void shouldRejectWhenCreditNotFound() {
        // Given
        String creditId = "nonExistentCredit";
        Transaction transaction = new Transaction();
        transaction.setCreditId(creditId);
        transaction.setType(Transaction.TypeEnum.SPENT);
        transaction.setAmount(100.0);

        when(creditRepository.findById(creditId)).thenReturn(Mono.empty());

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Crédito no encontrado"))
                .verify();
    }

    @Test
    void shouldValidateSpentTransactionWithSufficientCredit() {
        // Given
        String creditId = "credit123";
        Double amount = 200.0;
        Double availableCredit = 500.0;
        Double newAvailableCredit = availableCredit - amount;

        Transaction transaction = new Transaction();
        transaction.setCreditId(creditId);
        transaction.setType(Transaction.TypeEnum.SPENT);
        transaction.setAmount(amount);

        CreditBaseEntity credit = new CreditBaseEntity();
        credit.setCreditId(creditId);
        credit.setAmount(1000.0);
        credit.setAvailableCredit(availableCredit);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(credit));
        when(creditRepository.updateAvilableAmountByCreditId(creditId, newAvailableCredit))
                .thenReturn(Mono.empty());

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectNext(transaction)
                .verifyComplete();

        verify(creditRepository).updateAvilableAmountByCreditId(creditId, newAvailableCredit);
        verify(creditRepository, never()).save(any());
    }

    @Test
    void shouldRejectSpentTransactionWithInsufficientCredit() {
        // Given
        String creditId = "credit123";
        Double amount = 600.0;
        Double availableCredit = 500.0;

        Transaction transaction = new Transaction();
        transaction.setCreditId(creditId);
        transaction.setType(Transaction.TypeEnum.SPENT);
        transaction.setAmount(amount);

        CreditBaseEntity credit = new CreditBaseEntity();
        credit.setCreditId(creditId);
        credit.setAmount(1000.0);
        credit.setAvailableCredit(availableCredit);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(credit));

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Crédito disponible insuficiente"))
                .verify();

        verify(creditRepository, never()).updateAvilableAmountByCreditId(anyString(), anyDouble());
        verify(creditRepository, never()).save(any());
    }

    @Test
    void shouldValidatePaymentTransactionWithinLimit() {
        // Given
        String creditId = "credit123";
        Double amount = 200.0;
        Double availableCredit = 500.0;
        Double totalAmount = 1000.0;
        Double newAvailableCredit = availableCredit + amount;

        Transaction transaction = new Transaction();
        transaction.setCreditId(creditId);
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setAmount(amount);

        CreditBaseEntity credit = new CreditBaseEntity();
        credit.setCreditId(creditId);
        credit.setAmount(totalAmount);
        credit.setAvailableCredit(availableCredit);

        CreditBaseEntity updatedCredit = new CreditBaseEntity();
        updatedCredit.setCreditId(creditId);
        updatedCredit.setAmount(totalAmount);
        updatedCredit.setAvailableCredit(newAvailableCredit);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(credit));
        when(creditRepository.save(any(CreditBaseEntity.class))).thenReturn(Mono.just(updatedCredit));

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectNext(transaction)
                .verifyComplete();

        verify(creditRepository, never()).updateAvilableAmountByCreditId(anyString(), anyDouble());
        verify(creditRepository).save(any(CreditBaseEntity.class));
    }

    @Test
    void shouldRejectPaymentTransactionExceedingLimit() {
        // Given
        String creditId = "credit123";
        Double amount = 600.0;
        Double availableCredit = 500.0;
        Double totalAmount = 1000.0;

        Transaction transaction = new Transaction();
        transaction.setCreditId(creditId);
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setAmount(amount);

        CreditBaseEntity credit = new CreditBaseEntity();
        credit.setCreditId(creditId);
        credit.setAmount(totalAmount);
        credit.setAvailableCredit(availableCredit);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(credit));

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("El pago excede el límite del crédito"))
                .verify();

        verify(creditRepository, never()).updateAvilableAmountByCreditId(anyString(), anyDouble());
        verify(creditRepository, never()).save(any());
    }
}
