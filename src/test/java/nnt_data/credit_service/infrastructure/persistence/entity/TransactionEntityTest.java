package nnt_data.credit_service.infrastructure.persistence.entity;

import nnt_data.credit_service.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionEntityTest {

    @Test
    @DisplayName("Debería tener todos los atributos requeridos")
    void shouldHaveAllRequiredAttributes() {
        // Verificar que la clase tenga todos los atributos esperados
        Field[] fields = TransactionEntity.class.getDeclaredFields();

        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("transactionId")), "Debe tener un campo transactionId");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("date")), "Debe tener un campo date");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("type")), "Debe tener un campo type");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("amount")), "Debe tener un campo amount");
        assertTrue(Arrays.stream(fields).anyMatch(field -> field.getName().equals("creditId")), "Debe tener un campo creditId");
    }

    @Test
    @DisplayName("Debería tener anotación Document con nombre de colección correcto")
    void shouldHaveDocumentAnnotationWithCorrectCollectionName() {
        Document annotation = TransactionEntity.class.getAnnotation(Document.class);
        assertNotNull(annotation, "La clase debe tener la anotación @Document");
        assertEquals("credit_transaction", annotation.collection(), "La colección debe llamarse 'credit_transaction'");
    }

    @Test
    @DisplayName("Debería tener anotación Id en el campo transactionId")
    void shouldHaveIdAnnotationOnTransactionIdField() throws NoSuchFieldException {
        Field transactionIdField = TransactionEntity.class.getDeclaredField("transactionId");
        Id annotation = transactionIdField.getAnnotation(Id.class);
        assertNotNull(annotation, "El campo transactionId debe tener la anotación @Id");
    }

    @Test
    @DisplayName("Debería crear instancia con valores por defecto")
    void shouldCreateInstanceWithDefaultValues() {
        TransactionEntity entity = new TransactionEntity();

        assertNull(entity.getTransactionId());
        assertNull(entity.getDate());
        assertNull(entity.getType());
        assertNull(entity.getAmount());
        assertNull(entity.getCreditId());
    }

    @Test
    @DisplayName("Debería establecer y obtener valores correctamente")
    void shouldSetAndGetValuesCorrectly() {
        // Crear fecha de prueba
        Date testDate = new Date();

        // Crear instancia y establecer valores
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId("trans123");
        entity.setDate(testDate);
        entity.setType(Transaction.TypeEnum.SPENT);
        entity.setAmount(500.0);
        entity.setCreditId("credit456");

        // Verificar que los valores se hayan establecido correctamente
        assertEquals("trans123", entity.getTransactionId());
        assertEquals(testDate, entity.getDate());
        assertEquals(Transaction.TypeEnum.SPENT, entity.getType());
        assertEquals(500.0, entity.getAmount());
        assertEquals("credit456", entity.getCreditId());
    }

    @Test
    @DisplayName("Debería implementar equals y hashCode correctamente")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Crear fecha de prueba
        Date testDate = new Date();

        // Crear dos instancias con los mismos valores
        TransactionEntity entity1 = new TransactionEntity();
        entity1.setTransactionId("trans123");
        entity1.setDate(testDate);
        entity1.setType(Transaction.TypeEnum.SPENT);
        entity1.setAmount(500.0);
        entity1.setCreditId("credit456");

        TransactionEntity entity2 = new TransactionEntity();
        entity2.setTransactionId("trans123");
        entity2.setDate(testDate);
        entity2.setType(Transaction.TypeEnum.SPENT);
        entity2.setAmount(500.0);
        entity2.setCreditId("credit456");

        // Crear una instancia con valores diferentes
        TransactionEntity entity3 = new TransactionEntity();
        entity3.setTransactionId("trans789");
        entity3.setDate(new Date(testDate.getTime() + 1000));
        entity3.setType(Transaction.TypeEnum.PAYMENT);
        entity3.setAmount(1000.0);
        entity3.setCreditId("credit456");

        // Verificar equals y hashCode
        assertEquals(entity1, entity2, "Las entidades con los mismos valores deben ser iguales");
        assertEquals(entity1.hashCode(), entity2.hashCode(), "Los hashCodes de entidades iguales deben ser iguales");
        assertNotEquals(entity1, entity3, "Las entidades con diferentes valores no deben ser iguales");
        assertNotEquals(entity1.hashCode(), entity3.hashCode(), "Los hashCodes de entidades diferentes no deberían ser iguales");
    }

    @Test
    @DisplayName("Debería manejar tipos de transacción correctamente")
    void shouldHandleTransactionTypesCorrectly() {
        TransactionEntity entity = new TransactionEntity();

        // Probar con tipo SPENT
        entity.setType(Transaction.TypeEnum.SPENT);
        assertEquals(Transaction.TypeEnum.SPENT, entity.getType());

        // Probar con tipo PAYMENT
        entity.setType(Transaction.TypeEnum.PAYMENT);
        assertEquals(Transaction.TypeEnum.PAYMENT, entity.getType());
    }

    @Test
    @DisplayName("Debería manejar montos negativos y positivos")
    void shouldHandleNegativeAndPositiveAmounts() {
        TransactionEntity entity = new TransactionEntity();

        // Establecer monto positivo
        entity.setAmount(500.0);
        assertEquals(500.0, entity.getAmount());

        // Establecer monto negativo
        entity.setAmount(-300.0);
        assertEquals(-300.0, entity.getAmount());
    }

    @Test
    @DisplayName("Debería permitir fechas nulas")
    void shouldAllowNullDates() {
        TransactionEntity entity = new TransactionEntity();

        // Inicialmente la fecha es nula
        assertNull(entity.getDate());

        // Establecer una fecha
        Date testDate = new Date();
        entity.setDate(testDate);
        assertEquals(testDate, entity.getDate());

        // Volver a establecer fecha nula
        entity.setDate(null);
        assertNull(entity.getDate());
    }

}
