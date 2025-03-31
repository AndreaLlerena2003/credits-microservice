package nnt_data.credit_service.infrastructure.persistence.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nnt_data.credit_service.model.CreditType;
import nnt_data.credit_service.model.CustomerType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Getter
@Setter
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
