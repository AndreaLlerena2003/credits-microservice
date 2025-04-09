package nnt_data.credits_microservice.infrastructure.controller;

import nnt_data.credits_microservice.application.port.CreditOperationsPort;
import nnt_data.credits_microservice.application.port.TransactionOperationsPort;
import nnt_data.credits_microservice.model.CreditBase;
import nnt_data.credits_microservice.model.CreditCard;
import nnt_data.credits_microservice.model.CustomerType;
import nnt_data.credits_microservice.model.SimpleCredit;
import nnt_data.credits_microservice.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CreditControllerTest {

    @Mock
    private CreditOperationsPort creditOperationsPort;

    @Mock
    private TransactionOperationsPort transactionOperationsPort;

    @Mock
    private ServerWebExchange exchange;

    @InjectMocks
    private CreditController creditController;

    private CreditBase creditCard;
    private CreditBase simpleCredit;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        // Setup test data
        creditCard = new CreditCard();
        creditCard.setCreditId("card123");
        creditCard.setCustomerId("customer1");
        creditCard.setCustomerType(CustomerType.PERSONAL);
        creditCard.setAmount(5000.0);

        simpleCredit = new SimpleCredit();
        simpleCredit.setCreditId("simple123");
        simpleCredit.setCustomerId("customer2");
        simpleCredit.setCustomerType(CustomerType.BUSINESS);
        simpleCredit.setAmount(10000.0);

        transaction = new Transaction();
        transaction.setTransactionId("trans123");
        transaction.setCreditId("card123");
        transaction.setAmount(500.0);
        transaction.setDate(new Date());
        transaction.setType(Transaction.TypeEnum.SPENT);
    }

    @Test
    void shouldCreateTransaction() {
        // Given
        Mono<Transaction> transactionMono = Mono.just(transaction);
        when(transactionOperationsPort.createTransaction(any(Transaction.class)))
                .thenReturn(Mono.just(transaction));

        // When
        Mono<ResponseEntity<Transaction>> result = creditController.createTransaction(transactionMono, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(transaction, responseEntity.getBody());
                })
                .verifyComplete();
    }

    @Test
    void shouldGetCreditById() {
        // Given
        String creditId = "card123";
        when(creditOperationsPort.getByCreditId(creditId))
                .thenReturn(Mono.just(creditCard));

        // When
        Mono<ResponseEntity<CreditBase>> result = creditController.creditsCreditIdGet(creditId, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(creditCard, responseEntity.getBody());
                })
                .verifyComplete();
    }

    @Test
    void shouldUpdateCredit() {
        // Given
        String creditId = "card123";
        Mono<CreditBase> creditBaseMono = Mono.just(creditCard);
        when(creditOperationsPort.updateCredit(anyString(), any(CreditBase.class)))
                .thenReturn(Mono.just(creditCard));

        // When
        Mono<ResponseEntity<CreditBase>> result = creditController.creditsCreditIdPut(creditId, creditBaseMono, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(creditCard, responseEntity.getBody());
                })
                .verifyComplete();
    }

    @Test
    void shouldGetAllCredits() {
        // Given
        when(creditOperationsPort.getAllCredits())
                .thenReturn(Flux.just(creditCard, simpleCredit));

        // When
        Mono<ResponseEntity<Flux<CreditBase>>> result = creditController.creditsGet(exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    StepVerifier.create(responseEntity.getBody())
                            .expectNext(creditCard)
                            .expectNext(simpleCredit)
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateCredit() {
        // Given
        Mono<CreditBase> creditBaseMono = Mono.just(creditCard);
        when(creditOperationsPort.createCredit(any(CreditBase.class)))
                .thenReturn(Mono.just(creditCard));

        // When
        Mono<ResponseEntity<CreditBase>> result = creditController.creditsPost(creditBaseMono, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(creditCard, responseEntity.getBody());
                })
                .verifyComplete();
    }

    @Test
    void shouldDeleteCredit() {
        // Given
        String creditId = "card123";
        when(creditOperationsPort.deleteCredit(creditId))
                .thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<Void>> result = creditController.deleteCredit(creditId, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
                })
                .verifyComplete();

        verify(creditOperationsPort).deleteCredit(creditId);
    }

    @Test
    void shouldGetAllTransactions() {
        // Given
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId("trans456");

        when(transactionOperationsPort.getTransactions())
                .thenReturn(Flux.just(transaction, transaction2));

        // When
        Mono<ResponseEntity<Flux<Transaction>>> result = creditController.getAllTransactions(exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    StepVerifier.create(responseEntity.getBody())
                            .expectNext(transaction)
                            .expectNext(transaction2)
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void shouldGetTransactionsByCreditId() {
        // Given
        String creditId = "card123";
        when(transactionOperationsPort.getTransactionByCreditId(creditId))
                .thenReturn(Flux.just(transaction));

        // When
        Mono<ResponseEntity<Flux<Transaction>>> result = creditController.getTransactionsByCreditId(creditId, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    StepVerifier.create(responseEntity.getBody())
                            .expectNext(transaction)
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoTransactionsForCreditId() {
        // Given
        String creditId = "nonexistent";
        when(transactionOperationsPort.getTransactionByCreditId(creditId))
                .thenReturn(Flux.empty());

        // When
        Mono<ResponseEntity<Flux<Transaction>>> result = creditController.getTransactionsByCreditId(creditId, exchange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    StepVerifier.create(responseEntity.getBody())
                            .verifyComplete();
                })
                .verifyComplete();
    }
}