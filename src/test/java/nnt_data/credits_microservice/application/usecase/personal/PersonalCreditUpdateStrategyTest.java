package nnt_data.credits_microservice.application.usecase.personal;

import nnt_data.credits_microservice.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credits_microservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PersonalCreditUpdateStrategyTest {

    @Mock
    private CreditRepository creditRepository;

    private PersonalCreditUpdateStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PersonalCreditUpdateStrategy(creditRepository);
    }

    @Test
    void shouldRejectNonPersonalCustomer() {
        // Given
        CreditBase credit = new CreditBase();
        credit.setCustomerType(CustomerType.BUSINESS);

        // When
        Mono<CreditBase> result = strategy.updateCredit(credit);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Esta estrategia solo aplica para clientes personales"))
                .verify();
    }

    @Test
    void shouldSetAvailableCreditWhenCreditCardAndNullAvailableCredit() {
        // Given
        Double amount = 1000.0;
        CreditCard credit = new CreditCard();
        credit.setCustomerType(CustomerType.PERSONAL);
        credit.setType(CreditType.CREDIT_CARD);
        credit.setAmount(amount);
        credit.setAvailableCredit(null);

        // When
        Mono<CreditBase> result = strategy.updateCredit(credit);

        // Then
        StepVerifier.create(result)
                .assertNext(c -> {
                    CreditCard cc = (CreditCard) c;
                    assertEquals(amount, cc.getAvailableCredit());
                })
                .verifyComplete();
    }

    @Test
    void shouldNotChangeAvailableCreditWhenCreditCardAndAvailableCreditNotNull() {
        // Given
        Double amount = 1000.0;
        Double availableCredit = 500.0;
        CreditCard credit = new CreditCard();
        credit.setCustomerType(CustomerType.PERSONAL);
        credit.setType(CreditType.CREDIT_CARD);
        credit.setAmount(amount);
        credit.setAvailableCredit(availableCredit);

        // When
        Mono<CreditBase> result = strategy.updateCredit(credit);

        // Then
        StepVerifier.create(result)
                .assertNext(c -> {
                    CreditCard cc = (CreditCard) c;
                    assertEquals(availableCredit, cc.getAvailableCredit());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnSameInstanceWhenSimpleCredit() {
        // Given
        SimpleCredit credit = new SimpleCredit();
        credit.setCustomerType(CustomerType.PERSONAL);
        credit.setType(CreditType.SIMPLE_CREDIT);
        credit.setAmountPaid(300.0);

        // When
        Mono<CreditBase> result = strategy.updateCredit(credit);

        // Then
        StepVerifier.create(result)
                .assertNext(c -> {
                    assertTrue(c instanceof SimpleCredit);
                    SimpleCredit sc = (SimpleCredit) c;
                    assertEquals(credit, sc);
                    assertEquals(300.0, sc.getAmountPaid());
                })
                .verifyComplete();
    }

}
