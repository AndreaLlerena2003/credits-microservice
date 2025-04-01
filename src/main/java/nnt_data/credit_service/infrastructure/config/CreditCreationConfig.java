package nnt_data.credit_service.infrastructure.config;

import nnt_data.credit_service.application.usecase.BusinessCreditCreationStrategy;
import nnt_data.credit_service.application.usecase.CreditCreationStrategy;
import nnt_data.credit_service.application.usecase.PersonalCreditCreationStrategy;
import nnt_data.credit_service.model.CustomerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
/**
 * Configuración de estrategias de creación de créditos.
 *
 * - Define un bean que mapea CustomerType a la estrategia correspondiente.
 * - Incluye estrategias para clientes personales y empresariales.
 */
@Configuration
public class CreditCreationConfig {
    @Bean
    public Map<CustomerType, CreditCreationStrategy> creditCreationStrategies(
            PersonalCreditCreationStrategy personalStrategy,
            BusinessCreditCreationStrategy businessStrategy) {
        return Map.of(
                CustomerType.PERSONAL, personalStrategy,
                CustomerType.BUSINESS, businessStrategy
        );
    }
}
