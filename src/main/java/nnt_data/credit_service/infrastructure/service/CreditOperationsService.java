package nnt_data.credit_service.infrastructure.service;

import nnt_data.credit_service.application.usecase.CreditCreationStrategy;
import nnt_data.credit_service.domain.port.CreditOperationsPort;
import nnt_data.credit_service.infrastructure.persistence.mapper.CreditMapper;
import nnt_data.credit_service.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credit_service.model.CreditBase;
import nnt_data.credit_service.model.CustomerType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class CreditOperationsService implements CreditOperationsPort {

    private final Map<CustomerType, CreditCreationStrategy> creationStrategies;
    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    @Override
    public Mono<CreditBase> createCredit(CreditBase credit) {
        return executeCreationStrategy(credit)
                .flatMap(this::saveAccount);
    }

    @Override
    public Mono<CreditBase> updateCredit(String creditId,CreditBase credit) {
        return Mono.just(credit)
                .flatMap(c -> {
                    return creditRepository.findById(creditId)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("No existe un crédito con el ID: " + c.getCreditId())));
                })
                .then(executeCreationStrategy(credit))
                .flatMap(this::saveAccount);
    }

    @Override
    public Mono<CreditBase> getByCreditId(String creditId) {
        return creditRepository.findById(creditId)
                .flatMap(creditMapper::toDomain)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No existe un crédito con el ID: " + creditId)));
    }

    @Override
    public Flux<CreditBase> getAllCredits() {
        return creditRepository.findAll()
                .flatMap(creditMapper::toDomain);
    }

    public CreditOperationsService(Map<CustomerType, CreditCreationStrategy> creationStrategies,
                                    CreditRepository creditRepository,
                                    CreditMapper creditMapper) {
        this.creationStrategies = creationStrategies;
        this.creditRepository = creditRepository;
        this.creditMapper = creditMapper;
    }

    private Mono<CreditBase> executeCreationStrategy(CreditBase creditBase) {
        return Mono.just(creditBase)
                .filter(acc -> acc.getCustomerType() != null)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El tipo de cliente no puede ser null")))
                .flatMap(acc -> Mono.justOrEmpty(creationStrategies.get(acc.getCustomerType()))
                        .switchIfEmpty(Mono.error(
                                new IllegalArgumentException("Tipo de cliente no soportado: " + acc.getCustomerType())))
                        .flatMap(strategy -> strategy.createCredit(acc)));
    }

    private Mono<CreditBase> saveAccount(CreditBase creditBase) {
        return Mono.just(creditBase)
                .flatMap(creditMapper::toEntity)
                .flatMap(creditRepository::save)
                .flatMap(creditMapper::toDomain);
    }



}
