package nnt_data.credits_microservice.infrastructure.persistence.entity;

import lombok.Data;
import nnt_data.credits_microservice.model.Transaction;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
/**
 * Entidad TransactionEntity que representa una transacción en la base de datos.
 *
 * - transactionId: Identificador de la transacción.
 * - date: Fecha de la transacción.
 * - type: Tipo de transacción (enum).
 * - amount: Monto de la transacción.
 * - creditId: Identificador del crédito asociado.
 */
@Data
@Document(collection = "credit_transaction")
public class TransactionEntity {
    @Id
    private String transactionId;
    private Date date;
    private Transaction.TypeEnum type;
    private Double amount;
    private String creditId;
}
