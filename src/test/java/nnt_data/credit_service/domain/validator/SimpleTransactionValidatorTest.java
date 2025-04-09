package nnt_data.credit_service.domain.validator;

import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credit_service.infrastructure.persistence.mapper.CreditMapper;
import nnt_data.credit_service.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credit_service.model.CreditType;
import nnt_data.credit_service.model.SimpleCredit;
import nnt_data.credit_service.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleTransactionValidatorTest {

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private CreditMapper creditMapper;

    private SimpleTransactionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SimpleTransactionValidator(creditRepository, creditMapper);
    }

    @Test
    void shouldRejectNonPaymentTransactions() {
        // Given
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TypeEnum.SPENT);
        transaction.setCreditId("credit123");

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Solo se permiten transacciones de tipo payment para un credito simple"))
                .verify();

        verify(creditRepository, never()).findById(anyString());
    }

    @Test
    void shouldRejectTransactionWhenCreditNotFound() {
        // Given
        String creditId = "nonExistentCredit";
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setCreditId(creditId);

        when(creditRepository.findById(creditId)).thenReturn(Mono.empty());

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Crédito no encontrado o no es de tipo simple"))
                .verify();
    }

    @Test
    void shouldRejectTransactionWhenCreditTypeIsNotSimple() {
        // Given
        String creditId = "credit123";
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setCreditId(creditId);

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setType(CreditType.CREDIT_CARD);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(creditEntity));

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Crédito no encontrado o no es de tipo simple"))
                .verify();
    }

    @Test
    void shouldRejectTransactionWhenPaymentExceedsTotalAmount() {
        // Given
        String creditId = "credit123";
        Double amount = 300.0;
        Double totalAmount = 1000.0;
        Double currentAmountPaid = 800.0;

        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setCreditId(creditId);
        transaction.setAmount(amount);

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setType(CreditType.SIMPLE_CREDIT);

        SimpleCredit simpleCredit = new SimpleCredit();
        simpleCredit.setAmount(totalAmount);
        simpleCredit.setAmountPaid(currentAmountPaid);
        simpleCredit.setType(CreditType.SIMPLE_CREDIT);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(creditEntity));
        when(creditMapper.toDomain(creditEntity)).thenReturn(Mono.just(simpleCredit));

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("El pago excede el monto total del crédito"))
                .verify();
    }

    @Test
    void shouldRejectTransactionWhenCreditIsAlreadyFullyPaid() {
        // Given
        String creditId = "credit123";
        Double amount = 100.0;
        Double totalAmount = 1000.0;
        Double currentAmountPaid = 1000.0; // Ya pagado completamente

        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setCreditId(creditId);
        transaction.setAmount(amount);

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setType(CreditType.SIMPLE_CREDIT);

        SimpleCredit simpleCredit = new SimpleCredit();
        simpleCredit.setAmount(totalAmount);
        simpleCredit.setAmountPaid(currentAmountPaid);
        simpleCredit.setType(CreditType.SIMPLE_CREDIT);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(creditEntity));
        when(creditMapper.toDomain(creditEntity)).thenReturn(Mono.just(simpleCredit));

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("El pago excede el monto total del crédito"))
                .verify();
    }

    @Test
    void shouldProcessValidPaymentTransaction() {
        // Given
        String creditId = "credit123";
        Double amount = 200.0;
        Double totalAmount = 1000.0;
        Double currentAmountPaid = 300.0;
        Double newAmountPaid = currentAmountPaid + amount;

        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setCreditId(creditId);
        transaction.setAmount(amount);

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setType(CreditType.SIMPLE_CREDIT);

        SimpleCredit simpleCredit = new SimpleCredit();
        simpleCredit.setAmount(totalAmount);
        simpleCredit.setAmountPaid(currentAmountPaid);
        simpleCredit.setType(CreditType.SIMPLE_CREDIT);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(creditEntity));
        when(creditMapper.toDomain(creditEntity)).thenReturn(Mono.just(simpleCredit));
        when(creditMapper.toEntity(simpleCredit)).thenReturn(Mono.just(creditEntity));
        when(creditRepository.updateAmountPaidByCreditId(creditId, newAmountPaid)).thenReturn(Mono.empty());

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectNext(transaction)
                .verifyComplete();

        verify(creditRepository).updateAmountPaidByCreditId(creditId, newAmountPaid);
    }

    @Test
    void shouldHandleErrorDuringCreditUpdate() {
        // Given
        String creditId = "credit123";
        Double amount = 200.0;
        Double totalAmount = 1000.0;
        Double currentAmountPaid = 300.0;
        Double newAmountPaid = currentAmountPaid + amount;

        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setCreditId(creditId);
        transaction.setAmount(amount);

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setType(CreditType.SIMPLE_CREDIT);

        SimpleCredit simpleCredit = new SimpleCredit();
        simpleCredit.setAmount(totalAmount);
        simpleCredit.setAmountPaid(currentAmountPaid);
        simpleCredit.setType(CreditType.SIMPLE_CREDIT);

        RuntimeException dbError = new RuntimeException("Database error");

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(creditEntity));
        when(creditMapper.toDomain(creditEntity)).thenReturn(Mono.just(simpleCredit));
        when(creditMapper.toEntity(simpleCredit)).thenReturn(Mono.just(creditEntity));
        when(creditRepository.updateAmountPaidByCreditId(creditId, newAmountPaid)).thenReturn(Mono.error(dbError));

        // When
        Mono<Transaction> result = validator.validate(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Error al actualizar el crédito: Database error"))
                .verify();
    }
}
