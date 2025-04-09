package nnt_data.credits_microservice.application.usecase.business;

import nnt_data.credits_microservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BusinessCreditUpdateStrategyTest {
    private BusinessCreditUpdateStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BusinessCreditUpdateStrategy();
    }

    @Test
    void shouldRejectNonBusinessCustomer() {
        // Given
        CreditBase credit = mock(CreditBase.class);
        when(credit.getCustomerType()).thenReturn(CustomerType.PERSONAL);

        // When
        Mono<CreditBase> result = strategy.updateCredit(credit);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Esta estrategia solo aplica para clientes empresariales"))
                .verify();
    }

    @Test
    void shouldReturnSameInstanceWhenCreditCardType() {
        // Given
        CreditCard credit = new CreditCard();
        credit.setCustomerType(CustomerType.BUSINESS);
        credit.setType(CreditType.CREDIT_CARD);

        // When
        Mono<CreditBase> result = strategy.updateCredit(credit);

        // Then
        StepVerifier.create(result)
                .assertNext(updatedCredit -> {
                    assertTrue(updatedCredit instanceof CreditCard);
                    assertEquals(credit, updatedCredit);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnSameInstanceWhenSimpleCreditType() {
        // Given
        SimpleCredit credit = new SimpleCredit();
        credit.setCustomerType(CustomerType.BUSINESS);
        credit.setType(CreditType.SIMPLE_CREDIT);

        // When
        Mono<CreditBase> result = strategy.updateCredit(credit);

        // Then
        StepVerifier.create(result)
                .assertNext(updatedCredit -> {
                    assertTrue(updatedCredit instanceof SimpleCredit);
                    assertEquals(credit, updatedCredit);
                })
                .verifyComplete();
    }

}
