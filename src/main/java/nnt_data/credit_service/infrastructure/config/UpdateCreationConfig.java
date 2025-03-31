package nnt_data.credit_service.infrastructure.config;

import nnt_data.credit_service.application.usecase.*;
import nnt_data.credit_service.model.CustomerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

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
