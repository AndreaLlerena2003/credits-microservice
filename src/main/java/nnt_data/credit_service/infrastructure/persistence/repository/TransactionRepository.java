package nnt_data.credit_service.infrastructure.persistence.repository;

import nnt_data.credit_service.infrastructure.persistence.model.TransactionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<TransactionEntity, String> {
    Flux<TransactionEntity> findByCreditId(String creditId);
}
