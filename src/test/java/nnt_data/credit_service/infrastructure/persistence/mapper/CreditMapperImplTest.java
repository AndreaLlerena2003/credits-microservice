package nnt_data.credit_service.infrastructure.persistence.mapper;

import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credit_service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

public class CreditMapperImplTest {

    @InjectMocks
    private CreditMapperImpl creditMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería mapear CreditCard a CreditBaseEntity correctamente")
    void shouldMapCreditCardToEntityCorrectly() {
        // Crear un objeto CreditCard para prueba
        CreditCard creditCard = new CreditCard();
        creditCard.setCreditId("card123");
        creditCard.setCustomerId("customer456");
        creditCard.setCustomerType(CustomerType.PERSONAL);
        creditCard.setType(CreditType.CREDIT_CARD);
        creditCard.setAmount(5000.0);
        creditCard.setCardNumber("1234567890123456");
        creditCard.setAvailableCredit(4500.0);


        // Ejecutar el mapeo
        Mono<CreditBaseEntity> entityMono = creditMapper.toEntity(creditCard);

        // Verificar el resultado
        StepVerifier.create(entityMono)
                .assertNext(entity -> {
                    assertEquals("card123", entity.getCreditId());
                    assertEquals("customer456", entity.getCustomerId());
                    assertEquals(CustomerType.PERSONAL, entity.getCustomerType());
                    assertEquals(5000.0, entity.getAmount());
                    assertEquals(CreditType.CREDIT_CARD, entity.getType());
                    assertEquals("1234567890123456", entity.getCardNumber());
                    assertEquals(4500.0, entity.getAvailableCredit());
                    assertEquals(500.0, entity.getAmountPaid());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería mapear SimpleCredit a CreditBaseEntity correctamente")
    void shouldMapSimpleCreditToEntityCorrectly() {
        // Crear un objeto SimpleCredit para prueba
        SimpleCredit simpleCredit = new SimpleCredit();
        simpleCredit.setCreditId("simple123");
        simpleCredit.setCustomerId("customer789");
        simpleCredit.setCustomerType(CustomerType.BUSINESS);
        simpleCredit.setAmount(10000.0);
        simpleCredit.setAmountPaid(2000.0);
        simpleCredit.setType(CreditType.SIMPLE_CREDIT);

        // Ejecutar el mapeo
        Mono<CreditBaseEntity> entityMono = creditMapper.toEntity(simpleCredit);

        // Verificar el resultado
        StepVerifier.create(entityMono)
                .assertNext(entity -> {
                    assertEquals("simple123", entity.getCreditId());
                    assertEquals("customer789", entity.getCustomerId());
                    assertEquals(CustomerType.BUSINESS, entity.getCustomerType());
                    assertEquals(10000.0, entity.getAmount());
                    assertEquals(CreditType.SIMPLE_CREDIT, entity.getType());
                    assertNull(entity.getCardNumber());
                    assertNull(entity.getAvailableCredit());
                    assertEquals(2000.0, entity.getAmountPaid());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería mapear CreditBaseEntity a CreditCard correctamente")
    void shouldMapEntityToCreditCardCorrectly() {
        // Crear un objeto CreditBaseEntity para prueba
        CreditBaseEntity entity = new CreditBaseEntity();
        entity.setCreditId("card123");
        entity.setCustomerId("customer456");
        entity.setCustomerType(CustomerType.PERSONAL);
        entity.setAmount(5000.0);
        entity.setType(CreditType.CREDIT_CARD);
        entity.setCardNumber("1234567890123456");
        entity.setAvailableCredit(4500.0);
        entity.setAmountPaid(500.0);

        // Ejecutar el mapeo
        Mono<CreditBase> domainMono = creditMapper.toDomain(entity);

        // Verificar el resultado
        StepVerifier.create(domainMono)
                .assertNext(domain -> {
                    assertTrue(domain instanceof CreditCard);
                    CreditCard creditCard = (CreditCard) domain;

                    assertEquals("card123", creditCard.getCreditId());
                    assertEquals("customer456", creditCard.getCustomerId());
                    assertEquals(CustomerType.PERSONAL, creditCard.getCustomerType());
                    assertEquals(5000.0, creditCard.getAmount());
                    assertEquals("1234567890123456", creditCard.getCardNumber());
                    assertEquals(4500.0, creditCard.getAvailableCredit());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería mapear CreditBaseEntity a SimpleCredit correctamente")
    void shouldMapEntityToSimpleCreditCorrectly() {
        // Crear un objeto CreditBaseEntity para prueba
        CreditBaseEntity entity = new CreditBaseEntity();
        entity.setCreditId("simple123");
        entity.setCustomerId("customer789");
        entity.setCustomerType(CustomerType.BUSINESS);
        entity.setAmount(10000.0);
        entity.setType(CreditType.SIMPLE_CREDIT);
        entity.setAmountPaid(2000.0);

        // Ejecutar el mapeo
        Mono<CreditBase> domainMono = creditMapper.toDomain(entity);

        // Verificar el resultado
        StepVerifier.create(domainMono)
                .assertNext(domain -> {
                    assertTrue(domain instanceof SimpleCredit);
                    SimpleCredit simpleCredit = (SimpleCredit) domain;

                    assertEquals("simple123", simpleCredit.getCreditId());
                    assertEquals("customer789", simpleCredit.getCustomerId());
                    assertEquals(CustomerType.BUSINESS, simpleCredit.getCustomerType());
                    assertEquals(10000.0, simpleCredit.getAmount());
                    assertEquals(2000.0, simpleCredit.getAmountPaid());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería preservar valores nulos durante el mapeo")
    void shouldPreserveNullValuesInMapping() {
        // Crear un objeto CreditCard con algunos valores nulos
        CreditCard creditCard = new CreditCard();
        creditCard.setCreditId("card123");
        creditCard.setCustomerId(null); // Campo nulo
        creditCard.setCustomerType(CustomerType.PERSONAL);
        creditCard.setType(CreditType.CREDIT_CARD);
        creditCard.setAmount(null); // Campo nulo
        creditCard.setCardNumber("1234567890123456");

        // Ejecutar mapeo de ida y vuelta
        Mono<CreditBase> roundTripMono = creditMapper.toEntity(creditCard)
                .flatMap(creditMapper::toDomain);

        // Verificar que los valores nulos se preservan
        StepVerifier.create(roundTripMono)
                .assertNext(domain -> {
                    assertTrue(domain instanceof CreditCard);
                    CreditCard result = (CreditCard) domain;

                    assertEquals("card123", result.getCreditId());
                    assertNull(result.getCustomerId());
                    assertEquals(CustomerType.PERSONAL, result.getCustomerType());
                    assertNull(result.getAmount());
                    assertEquals("1234567890123456", result.getCardNumber());
                })
                .verifyComplete();
    }

}
