package nnt_data.credit_service.infrastructure.persistence.mapper;

import nnt_data.credit_service.infrastructure.persistence.entity.CreditBaseEntity;
import nnt_data.credit_service.model.CreditBase;
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
