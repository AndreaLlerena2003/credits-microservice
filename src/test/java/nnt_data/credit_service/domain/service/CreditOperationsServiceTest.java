package nnt_data.credit_service.domain.service;


import nnt_data.credit_service.application.usecase.CreditCreationStrategy;
import nnt_data.credit_service.application.usecase.UpdateCreationStrategy;
import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credit_service.infrastructure.persistence.mapper.CreditMapper;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditOperationsServiceTest {

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private CreditMapper creditMapper;

    @Mock
    private CreditCreationStrategy personalCreationStrategy;

    @Mock
    private CreditCreationStrategy businessCreationStrategy;

    @Mock
    private UpdateCreationStrategy personalUpdateStrategy;

    @Mock
    private UpdateCreationStrategy businessUpdateStrategy;

    private Map<CustomerType, CreditCreationStrategy> creationStrategies;
    private Map<CustomerType, UpdateCreationStrategy> updateStrategies;

    private CreditOperationsService creditOperationsService;

    @BeforeEach
    void setUp() {
        creationStrategies = new HashMap<>();
        creationStrategies.put(CustomerType.PERSONAL, personalCreationStrategy);
        creationStrategies.put(CustomerType.BUSINESS, businessCreationStrategy);

        updateStrategies = new HashMap<>();
        updateStrategies.put(CustomerType.PERSONAL, personalUpdateStrategy);
        updateStrategies.put(CustomerType.BUSINESS, businessUpdateStrategy);

        creditOperationsService = new CreditOperationsService(
                creationStrategies,
                creditRepository,
                creditMapper,
                updateStrategies
        );
    }

    @Test
    void shouldCreateCreditWithPersonalStrategy() {
        // Given
        CreditBase credit = new SimpleCredit();
        credit.setCustomerType(CustomerType.PERSONAL);
        credit.setCustomerId("cust123");
        credit.setAmount(1000.0);

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setCreditId("credit123");

        when(personalCreationStrategy.createCredit(credit)).thenReturn(Mono.just(credit));
        when(creditMapper.toEntity(credit)).thenReturn(Mono.just(creditEntity));
        when(creditRepository.save(creditEntity)).thenReturn(Mono.just(creditEntity));
        when(creditMapper.toDomain(creditEntity)).thenReturn(Mono.just(credit));

        // When
        Mono<CreditBase> result = creditOperationsService.createCredit(credit);

        // Then
        StepVerifier.create(result)
                .expectNext(credit)
                .verifyComplete();

        verify(personalCreationStrategy).createCredit(credit);
        verify(businessCreationStrategy, never()).createCredit(any());
    }

    @Test
    void shouldCreateCreditWithBusinessStrategy() {
        // Given
        CreditBase credit = new CreditCard();
        credit.setCustomerType(CustomerType.BUSINESS);
        credit.setCustomerId("business123");
        credit.setAmount(5000.0);

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setCreditId("credit456");

        when(businessCreationStrategy.createCredit(credit)).thenReturn(Mono.just(credit));
        when(creditMapper.toEntity(credit)).thenReturn(Mono.just(creditEntity));
        when(creditRepository.save(creditEntity)).thenReturn(Mono.just(creditEntity));
        when(creditMapper.toDomain(creditEntity)).thenReturn(Mono.just(credit));

        // When
        Mono<CreditBase> result = creditOperationsService.createCredit(credit);

        // Then
        StepVerifier.create(result)
                .expectNext(credit)
                .verifyComplete();

        verify(businessCreationStrategy).createCredit(credit);
        verify(personalCreationStrategy, never()).createCredit(any());
    }

    @Test
    void shouldUpdateCreditWithPersonalStrategy() {
        // Given
        String creditId = "credit123";
        CreditBase credit = new SimpleCredit();
        credit.setCustomerType(CustomerType.PERSONAL);
        credit.setCustomerId("cust123");
        credit.setAmount(1500.0); // Monto actualizado

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setCreditId(creditId);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(creditEntity));
        when(creditMapper.toDomain(creditEntity)).thenReturn(Mono.just(credit));
        when(personalUpdateStrategy.updateCredit(credit)).thenReturn(Mono.just(credit));
        when(creditMapper.toEntity(credit)).thenReturn(Mono.just(creditEntity));
        when(creditRepository.save(creditEntity)).thenReturn(Mono.just(creditEntity));

        // When
        Mono<CreditBase> result = creditOperationsService.updateCredit(creditId, credit);

        // Then
        StepVerifier.create(result)
                .expectNext(credit)
                .verifyComplete();

        verify(personalUpdateStrategy).updateCredit(credit);
        verify(businessUpdateStrategy, never()).updateCredit(any());
    }

    @Test
    void shouldGetCreditById() {
        // Given
        String creditId = "credit123";
        CreditBase credit = new SimpleCredit();
        credit.setCustomerType(CustomerType.PERSONAL);

        CreditBaseEntity creditEntity = new CreditBaseEntity();
        creditEntity.setCreditId(creditId);

        when(creditRepository.findById(creditId)).thenReturn(Mono.just(creditEntity));
        when(creditMapper.toDomain(creditEntity)).thenReturn(Mono.just(credit));

        // When
        Mono<CreditBase> result = creditOperationsService.getByCreditId(creditId);

        // Then
        StepVerifier.create(result)
                .expectNext(credit)
                .verifyComplete();
    }

    @Test
    void shouldGetAllCredits() {
        // Given
        CreditBaseEntity entity1 = new CreditBaseEntity();
        entity1.setCreditId("credit1");

        CreditBaseEntity entity2 = new CreditBaseEntity();
        entity2.setCreditId("credit2");

        CreditBase credit1 = new SimpleCredit();
        CreditBase credit2 = new CreditCard();

        when(creditRepository.findAll()).thenReturn(Flux.just(entity1, entity2));
        when(creditMapper.toDomain(entity1)).thenReturn(Mono.just(credit1));
        when(creditMapper.toDomain(entity2)).thenReturn(Mono.just(credit2));

        // When
        Flux<CreditBase> result = creditOperationsService.getAllCredits();

        // Then
        StepVerifier.create(result)
                .expectNext(credit1, credit2)
                .verifyComplete();
    }

}
