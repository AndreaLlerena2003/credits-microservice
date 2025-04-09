package nnt_data.credit_service.infrastructure.persistence.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nnt_data.credit_service.model.CreditType;
import nnt_data.credit_service.model.CustomerType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * Entidad CreditBaseEntity que representa un crédito en la base de datos.
 *
 * - creditId: Identificador del crédito.
 * - customerId: Identificador del cliente.
 * - customerType: Tipo de cliente (personal o empresarial).
 * - amount: Monto del crédito.
 * - type: Tipo de crédito (tarjeta de crédito, crédito simple, etc.).
 * - cardNumber: Número de tarjeta de crédito (si aplica).
 * - availableCredit: Crédito disponible (si aplica).
 * - amountPaid: Monto pagado del crédito (si aplica).
 */

@Data
@Document(collection = "credits")
public class CreditBaseEntity {
    @Id
    private String creditId;
    private String customerId;
    private CustomerType customerType;
    private Double amount;
    private CreditType type;
    private String cardNumber;
    private Double availableCredit;
    private Double amountPaid;
}
