package nnt_data.credit_service.application.usecase.business;

import nnt_data.credit_service.application.usecase.business.BusinessCreditCreationStrategy;
import nnt_data.credit_service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BusinessCreditCreationStrategyTest {
    private BusinessCreditCreationStrategy strategy;

    @BeforeEach
    public void setUp() {
        strategy = new BusinessCreditCreationStrategy();
    }

    @Test
    void shouldRejectNonBusinessCustomer(){
        CreditBase credit = mock(CreditBase.class);
        when(credit.getCustomerType()).thenReturn(CustomerType.PERSONAL);

        Mono<CreditBase> result = strategy.createCredit(credit);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Esta estrategia solo aplica para clientes empresariales"))
                .verify();
    }

    @Test
    void shouldSetAvailableCreditWhenCreditCardAndNullAvailableCredit(){
        Double amount = 1000.0;
        CreditCard credit = new CreditCard();
        credit.setCustomerType(CustomerType.BUSINESS);
        credit.setType(CreditType.CREDIT_CARD);
        credit.setAmount(amount);
        credit.setAvailableCredit(null);

        Mono<CreditBase> result = strategy.createCredit(credit);

        StepVerifier.create(result)
                .assertNext(c -> {
                    CreditCard cc = (CreditCard) c;
                    assertEquals(amount, cc.getAvailableCredit());
                });
    }

    @Test
    void shouldNotChangeAvailableCreditWhenCreditCardAndAvailableCreditNotNull() {
        // Given
        Double amount = 1000.0;
        Double availableCredit = 500.0;
        CreditCard credit = new CreditCard();
        credit.setCustomerType(CustomerType.BUSINESS);
        credit.setType(CreditType.CREDIT_CARD);
        credit.setAmount(amount);
        credit.setAvailableCredit(availableCredit);

        // When
        Mono<CreditBase> result = strategy.createCredit(credit);

        // Then
        StepVerifier.create(result)
                .assertNext(c -> {
                    CreditCard cc = (CreditCard) c;
                    assertEquals(availableCredit, cc.getAvailableCredit());
                })
                .verifyComplete();
    }

    @Test
    void shouldSetAmountPaidWhenSimpleCreditAndNullAmountPaid() {
        // Given
        SimpleCredit credit = new SimpleCredit();
        credit.setCustomerType(CustomerType.BUSINESS);
        credit.setType(CreditType.SIMPLE_CREDIT);
        credit.setAmountPaid(null);

        // When
        Mono<CreditBase> result = strategy.createCredit(credit);

        // Then
        StepVerifier.create(result)
                .assertNext(c -> {
                    SimpleCredit sc = (SimpleCredit) c;
                    assertEquals(0.0, sc.getAmountPaid());
                })
                .verifyComplete();
    }

    @Test
    void shouldNotChangeAmountPaidWhenSimpleCreditAndAmountPaidNotNull() {
        // Given
        Double amountPaid = 300.0;
        SimpleCredit credit = new SimpleCredit();
        credit.setCustomerType(CustomerType.BUSINESS);
        credit.setType(CreditType.SIMPLE_CREDIT);
        credit.setAmountPaid(amountPaid);

        // When
        Mono<CreditBase> result = strategy.createCredit(credit);

        // Then
        StepVerifier.create(result)
                .assertNext(c -> {
                    SimpleCredit sc = (SimpleCredit) c;
                    assertEquals(amountPaid, sc.getAmountPaid());
                })
                .verifyComplete();
    }

}
