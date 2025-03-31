package nnt_data.credit_service.infrastructure.persistence.mapper;

import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credit_service.model.CreditBase;
import reactor.core.publisher.Mono;

public interface CreditMapper {
    Mono<CreditBaseEntity> toEntity(CreditBase creditBase);
    Mono<CreditBase> toDomain(CreditBaseEntity creditBaseEntity);
}
