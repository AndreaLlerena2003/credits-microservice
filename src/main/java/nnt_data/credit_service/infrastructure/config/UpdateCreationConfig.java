package nnt_data.credit_service.infrastructure.config;

import nnt_data.credit_service.application.usecase.*;
import nnt_data.credit_service.model.CustomerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
/**
 * Configuración de estrategias de actualización de créditos.
 *
 * - Define un bean que mapea CustomerType a la estrategia correspondiente.
 * - Incluye estrategias para clientes personales y empresariales.
 */
@Configuration
public class UpdateCreationConfig {
    @Bean
    public Map<CustomerType, UpdateCreationStrategy> creditUpdateStrategies(
            PersonalCreditUpdateStrategy personalStrategy,
            BusinessCreditUpdateStrategy businessStrategy) {
        return Map.of(
                CustomerType.PERSONAL, personalStrategy,
                CustomerType.BUSINESS, businessStrategy
        );
    }
}
