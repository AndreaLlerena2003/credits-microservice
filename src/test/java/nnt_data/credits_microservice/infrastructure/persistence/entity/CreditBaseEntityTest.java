package nnt_data.credits_microservice.infrastructure.persistence.entity;

import nnt_data.credits_microservice.model.CreditType;
import nnt_data.credits_microservice.model.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CreditBaseEntityTest {

    @Test
    @DisplayName("Debería tener todos los atributos requeridos")
    void shouldHaveAllRequiredAttributes() {
        // Verificar que la clase tenga todos los atributos esperados
        Field[] fields = CreditBaseEntity.class.getDeclaredFields();

        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("creditId")), "Debe tener un campo creditId");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("customerId")), "Debe tener un campo customerId");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("customerType")), "Debe tener un campo customerType");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("amount")), "Debe tener un campo amount");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("type")), "Debe tener un campo type");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("cardNumber")), "Debe tener un campo cardNumber");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("availableCredit")), "Debe tener un campo availableCredit");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("amountPaid")), "Debe tener un campo amountPaid");
    }

    @Test
    @DisplayName("Debería tener anotación Document con nombre de colección correcto")
    void shouldHaveDocumentAnnotationWithCorrectCollectionName() {
        Document annotation = CreditBaseEntity.class.getAnnotation(Document.class);
        assertNotNull(annotation, "La clase debe tener la anotación @Document");
        assertEquals("credits", annotation.collection(), "La colección debe llamarse 'credits'");
    }

    @Test
    @DisplayName("Debería tener anotación Id en el campo creditId")
    void shouldHaveIdAnnotationOnCreditIdField() throws NoSuchFieldException {
        Field creditIdField = CreditBaseEntity.class.getDeclaredField("creditId");
        Id annotation = creditIdField.getAnnotation(Id.class);
        assertNotNull(annotation, "El campo creditId debe tener la anotación @Id");
    }

    @Test
    @DisplayName("Debería crear instancia con valores por defecto")
    void shouldCreateInstanceWithDefaultValues() {
        CreditBaseEntity entity = new CreditBaseEntity();

        assertNull(entity.getCreditId());
        assertNull(entity.getCustomerId());
        assertNull(entity.getCustomerType());
        assertNull(entity.getAmount());
        assertNull(entity.getType());
        assertNull(entity.getCardNumber());
        assertNull(entity.getAvailableCredit());
        assertNull(entity.getAmountPaid());
    }

    @Test
    @DisplayName("Debería establecer y obtener valores correctamente")
    void shouldSetAndGetValuesCorrectly() {
        // Crear instancia y establecer valores
        CreditBaseEntity entity = new CreditBaseEntity();
        entity.setCreditId("credit123");
        entity.setCustomerId("customer456");
        entity.setCustomerType(CustomerType.PERSONAL);
        entity.setAmount(5000.0);
        entity.setType(CreditType.CREDIT_CARD);
        entity.setCardNumber("1234567890123456");
        entity.setAvailableCredit(4000.0);
        entity.setAmountPaid(1000.0);

        // Verificar que los valores se hayan establecido correctamente
        assertEquals("credit123", entity.getCreditId());
        assertEquals("customer456", entity.getCustomerId());
        assertEquals(CustomerType.PERSONAL, entity.getCustomerType());
        assertEquals(5000.0, entity.getAmount());
        assertEquals(CreditType.CREDIT_CARD, entity.getType());
        assertEquals("1234567890123456", entity.getCardNumber());
        assertEquals(4000.0, entity.getAvailableCredit());
        assertEquals(1000.0, entity.getAmountPaid());
    }

    @Test
    @DisplayName("Debería implementar equals y hashCode correctamente")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Crear dos instancias con los mismos valores
        CreditBaseEntity entity1 = new CreditBaseEntity();
        entity1.setCreditId("credit123");
        entity1.setCustomerId("customer456");
        entity1.setCustomerType(CustomerType.PERSONAL);
        entity1.setAmount(5000.0);
        entity1.setType(CreditType.CREDIT_CARD);
        entity1.setCardNumber("1234567890123456");
        entity1.setAvailableCredit(4000.0);
        entity1.setAmountPaid(1000.0);

        CreditBaseEntity entity2 = new CreditBaseEntity();
        entity2.setCreditId("credit123");
        entity2.setCustomerId("customer456");
        entity2.setCustomerType(CustomerType.PERSONAL);
        entity2.setAmount(5000.0);
        entity2.setType(CreditType.CREDIT_CARD);
        entity2.setCardNumber("1234567890123456");
        entity2.setAvailableCredit(4000.0);
        entity2.setAmountPaid(1000.0);

        // Crear una instancia con valores diferentes
        CreditBaseEntity entity3 = new CreditBaseEntity();
        entity3.setCreditId("credit789");
        entity3.setCustomerId("customer456");
        entity3.setCustomerType(CustomerType.BUSINESS);
        entity3.setAmount(10000.0);
        entity3.setType(CreditType.SIMPLE_CREDIT);

        // Verificar equals y hashCode
        assertEquals(entity1, entity2, "Las entidades con los mismos valores deben ser iguales");
        assertEquals(entity1.hashCode(), entity2.hashCode(), "Los hashCodes de entidades iguales deben ser iguales");
        assertNotEquals(entity1, entity3, "Las entidades con diferentes valores no deben ser iguales");
        assertNotEquals(entity1.hashCode(), entity3.hashCode(), "Los hashCodes de entidades diferentes no deberían ser iguales");
    }

    @Test
    @DisplayName("Debería manejar tipos de cliente correctamente")
    void shouldHandleCustomerTypesCorrectly() {
        CreditBaseEntity entity = new CreditBaseEntity();

        // Probar con cliente personal
        entity.setCustomerType(CustomerType.PERSONAL);
        assertEquals(CustomerType.PERSONAL, entity.getCustomerType());

        // Probar con cliente empresarial
        entity.setCustomerType(CustomerType.BUSINESS);
        assertEquals(CustomerType.BUSINESS, entity.getCustomerType());
    }

    @Test
    @DisplayName("Debería manejar tipos de crédito correctamente")
    void shouldHandleCreditTypesCorrectly() {
        CreditBaseEntity entity = new CreditBaseEntity();

        // Probar con tarjeta de crédito
        entity.setType(CreditType.CREDIT_CARD);
        assertEquals(CreditType.CREDIT_CARD, entity.getType());

        // Probar con crédito simple
        entity.setType(CreditType.SIMPLE_CREDIT);
        assertEquals(CreditType.SIMPLE_CREDIT, entity.getType());
    }

}
