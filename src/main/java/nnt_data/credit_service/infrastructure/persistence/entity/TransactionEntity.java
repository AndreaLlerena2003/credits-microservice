package nnt_data.credit_service.infrastructure.persistence.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nnt_data.credit_service.model.Transaction;
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
@Getter
@Setter
@Document(collection = "credit_transaction")
public class TransactionEntity {
    @Id
    private String transactionId;
    private Date date;
    private Transaction.TypeEnum type;
    private Double amount;
    private String creditId;
}
