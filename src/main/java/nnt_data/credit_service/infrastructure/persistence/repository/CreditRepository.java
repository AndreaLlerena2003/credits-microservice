package nnt_data.credit_service.infrastructure.persistence.repository;

import nnt_data.credit_service.infrastructure.persistence.model.CreditBaseEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CreditRepository extends ReactiveMongoRepository<CreditBaseEntity, String> {

}
