package nnt_data.credit_service.application.usecase.personal;

import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credit_service.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credit_service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonalCreditCreationStrategyTest {

    @Mock
    private CreditRepository creditRepository;

    private PersonalCreditCreationStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PersonalCreditCreationStrategy(creditRepository);
    }

    @Test
    void shouldRejectNonPersonalCustomer() {
        // Given
        CreditBase credit = new CreditBase();
        credit.setCustomerType(CustomerType.BUSINESS);

        // When
        Mono<CreditBase> result = strategy.createCredit(credit);

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
        String customerId = "customer123";
        Double amount = 1000.0;
        CreditCard credit = new CreditCard();
        credit.setCustomerId(customerId);
        credit.setCustomerType(CustomerType.PERSONAL);
        credit.setType(CreditType.CREDIT_CARD);
        credit.setAmount(amount);
        credit.setAvailableCredit(null);

        // When
        Mono<CreditBase> result = strategy.createCredit(credit);

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
        String customerId = "customer123";
        Double amount = 1000.0;
        Double availableCredit = 500.0;
        CreditCard credit = new CreditCard();
        credit.setCustomerId(customerId);
        credit.setCustomerType(CustomerType.PERSONAL);
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
    void shouldSetAmountPaidWhenSimpleCreditAndNullAmountPaidAndNoExistingCredits() {
        // Given
        String customerId = "customer123";
        SimpleCredit credit = new SimpleCredit();
        credit.setCustomerId(customerId);
        credit.setCustomerType(CustomerType.PERSONAL);
        credit.setType(CreditType.SIMPLE_CREDIT);
        credit.setAmountPaid(null);

        when(creditRepository.findAll()).thenReturn(Flux.empty());

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
    void shouldNotChangeAmountPaidWhenSimpleCreditAndAmountPaidNotNullAndNoExistingCredits() {
        // Given
        String customerId = "customer123";
        Double amountPaid = 300.0;
        SimpleCredit credit = new SimpleCredit();
        credit.setCustomerId(customerId);
        credit.setCustomerType(CustomerType.PERSONAL);
        credit.setType(CreditType.SIMPLE_CREDIT);
        credit.setAmountPaid(amountPaid);

        when(creditRepository.findAll()).thenReturn(Flux.empty());

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

    @Test
    void shouldRejectSimpleCreditWhenCustomerAlreadyHasOneActive() {
        // Given
        String customerId = "customer123";
        CreditBaseEntity existingCredit = new CreditBaseEntity();
        existingCredit.setCustomerId(customerId);
        existingCredit.setType(CreditType.SIMPLE_CREDIT);

        SimpleCredit newCredit = new SimpleCredit();
        newCredit.setCustomerId(customerId);
        newCredit.setCustomerType(CustomerType.PERSONAL);
        newCredit.setType(CreditType.SIMPLE_CREDIT);

        when(creditRepository.findAll()).thenReturn(Flux.just(existingCredit));

        // When
        Mono<CreditBase> result = strategy.createCredit(newCredit);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Cliente personal ya tiene un crédito simple activo"))
                .verify();
    }


}
