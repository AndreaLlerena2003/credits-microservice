package nnt_data.credits_microservice.domain.service;

import nnt_data.credits_microservice.domain.validator.TransactionValidator;
import nnt_data.credits_microservice.domain.validator.ValidatorFactory;
import nnt_data.credits_microservice.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credits_microservice.infrastructure.persistence.entity.TransactionEntity;
import nnt_data.credits_microservice.infrastructure.persistence.mapper.TransactionMapper;
import nnt_data.credits_microservice.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credits_microservice.infrastructure.persistence.repository.TransactionRepository;
import nnt_data.credits_microservice.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionOperationServiceTest {

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private ValidatorFactory validatorFactory;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private TransactionValidator transactionValidator;

    @InjectMocks
    private TransactionOperationService transactionOperationService;

    private Transaction transaction;
    private TransactionEntity transactionEntity;
    private CreditBaseEntity creditEntity;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setCreditId("credit123");
        transaction.setAmount(100.0);
        transaction.setType(Transaction.TypeEnum.SPENT);

        transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId("trans123");
        transactionEntity.setCreditId("credit123");

        creditEntity = new CreditBaseEntity();
        creditEntity.setCreditId("credit123");
    }

    @Test
    void shouldCreateTransaction() {
        // Given
        when(creditRepository.findById("credit123")).thenReturn(Mono.just(creditEntity));
        when(validatorFactory.getTransactionValidator(any(CreditBaseEntity.class))).thenReturn(transactionValidator);
        when(transactionValidator.validate(any(Transaction.class))).thenReturn(Mono.just(transaction));
        when(transactionMapper.toEntity(any(Transaction.class))).thenReturn(Mono.just(transactionEntity));
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(Mono.just(transactionEntity));
        when(transactionMapper.toDomain(any(TransactionEntity.class))).thenReturn(Mono.just(transaction));

        // When
        Mono<Transaction> result = transactionOperationService.createTransaction(transaction);

        // Then
        StepVerifier.create(result)
                .expectNext(transaction)
                .verifyComplete();

        verify(validatorFactory).getTransactionValidator(creditEntity);
        verify(transactionValidator).validate(any(Transaction.class));
        verify(transactionMapper).toEntity(any(Transaction.class));
        verify(transactionRepository).save(transactionEntity);
        verify(transactionMapper).toDomain(transactionEntity);
    }

    @Test
    void shouldFailWhenCreditNotFound() {
        // Given
        when(creditRepository.findById("credit123")).thenReturn(Mono.empty());

        // When
        Mono<Transaction> result = transactionOperationService.createTransaction(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Credit not found"))
                .verify();
    }

    @Test
    void shouldFailWhenValidationFails() {
        // Given
        String errorMessage = "Insufficient available credit";

        when(creditRepository.findById("credit123")).thenReturn(Mono.just(creditEntity));
        when(validatorFactory.getTransactionValidator(any(CreditBaseEntity.class))).thenReturn(transactionValidator);
        when(transactionValidator.validate(any(Transaction.class))).thenReturn(Mono.error(new RuntimeException(errorMessage)));

        // When
        Mono<Transaction> result = transactionOperationService.createTransaction(transaction);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(errorMessage))
                .verify();
    }

    @Test
    void shouldGetAllTransactions() {
        // Given
        TransactionEntity transactionEntity1 = new TransactionEntity();
        transactionEntity1.setTransactionId("trans1");

        TransactionEntity transactionEntity2 = new TransactionEntity();
        transactionEntity2.setTransactionId("trans2");

        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId("trans1");

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId("trans2");

        when(transactionRepository.findAll()).thenReturn(Flux.just(transactionEntity1, transactionEntity2));
        when(transactionMapper.toDomain(transactionEntity1)).thenReturn(Mono.just(transaction1));
        when(transactionMapper.toDomain(transactionEntity2)).thenReturn(Mono.just(transaction2));

        // When
        Flux<Transaction> result = transactionOperationService.getTransactions();

        // Then
        StepVerifier.create(result)
                .expectNext(transaction1)
                .expectNext(transaction2)
                .verifyComplete();
    }

    @Test
    void shouldGetTransactionsByCreditId() {
        // Given
        String creditId = "credit123";

        TransactionEntity transactionEntity1 = new TransactionEntity();
        transactionEntity1.setTransactionId("trans1");
        transactionEntity1.setCreditId(creditId);

        TransactionEntity transactionEntity2 = new TransactionEntity();
        transactionEntity2.setTransactionId("trans2");
        transactionEntity2.setCreditId(creditId);

        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId("trans1");
        transaction1.setCreditId(creditId);

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId("trans2");
        transaction2.setCreditId(creditId);

        when(transactionRepository.findByCreditId(creditId)).thenReturn(Flux.just(transactionEntity1, transactionEntity2));
        when(transactionMapper.toDomain(transactionEntity1)).thenReturn(Mono.just(transaction1));
        when(transactionMapper.toDomain(transactionEntity2)).thenReturn(Mono.just(transaction2));

        // When
        Flux<Transaction> result = transactionOperationService.getTransactionByCreditId(creditId);

        // Then
        StepVerifier.create(result)
                .expectNext(transaction1)
                .expectNext(transaction2)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoTransactionsFoundForCreditId() {
        // Given
        String creditId = "nonExistentCredit";

        when(transactionRepository.findByCreditId(creditId)).thenReturn(Flux.empty());

        // When
        Flux<Transaction> result = transactionOperationService.getTransactionByCreditId(creditId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldHandleErrorWhenGettingTransactionsByCreditId() {
        // Given
        String creditId = "credit123";
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(transactionRepository.findByCreditId(creditId)).thenReturn(Flux.error(repositoryException));

        // When
        Flux<Transaction> result = transactionOperationService.getTransactionByCreditId(creditId);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Error al obtener las transacciones por ID del credito"))
                .verify();
    }

    @Test
    void shouldSetDateWhenCreatingTransaction() {
        // Given
        Transaction transactionWithoutDate = new Transaction();
        transactionWithoutDate.setCreditId("credit123");
        transactionWithoutDate.setAmount(100.0);
        transactionWithoutDate.setType(Transaction.TypeEnum.SPENT);

        when(creditRepository.findById("credit123")).thenReturn(Mono.just(creditEntity));
        when(validatorFactory.getTransactionValidator(any(CreditBaseEntity.class))).thenReturn(transactionValidator);
        when(transactionValidator.validate(any(Transaction.class))).thenReturn(Mono.just(transactionWithoutDate));
        when(transactionMapper.toEntity(any(Transaction.class))).thenReturn(Mono.just(transactionEntity));
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(Mono.just(transactionEntity));
        when(transactionMapper.toDomain(any(TransactionEntity.class))).thenReturn(Mono.just(transaction));

        // When
        Mono<Transaction> result = transactionOperationService.createTransaction(transactionWithoutDate);

        // Then
        StepVerifier.create(result)
                .expectNext(transaction)
                .verifyComplete();

        // Verificar que se establece la fecha
        verify(transactionValidator).validate(any(Transaction.class));
    }
}