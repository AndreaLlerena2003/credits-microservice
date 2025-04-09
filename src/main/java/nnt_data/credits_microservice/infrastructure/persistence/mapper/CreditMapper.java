package nnt_data.credits_microservice.infrastructure.persistence.mapper;

import nnt_data.credits_microservice.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credits_microservice.model.CreditBase;
import reactor.core.publisher.Mono;
/**
 * Interfaz CreditMapper para mapear entre CreditBase y CreditBaseEntity.
 *
 * - toEntity: Convierte un CreditBase a CreditBaseEntity.
 * - toDomain: Convierte un CreditBaseEntity a CreditBase.
 */
public interface CreditMapper {
    Mono<CreditBaseEntity> toEntity(CreditBase creditBase);
    Mono<CreditBase> toDomain(CreditBaseEntity creditBaseEntity);
}
