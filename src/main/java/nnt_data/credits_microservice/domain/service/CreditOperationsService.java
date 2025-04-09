package nnt_data.credits_microservice.domain.service;

import nnt_data.credits_microservice.application.usecase.CreditCreationStrategy;
import nnt_data.credits_microservice.application.port.CreditOperationsPort;
import nnt_data.credits_microservice.application.usecase.UpdateCreationStrategy;
import nnt_data.credits_microservice.infrastructure.persistence.mapper.CreditMapper;
import nnt_data.credits_microservice.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credits_microservice.model.CreditBase;
import nnt_data.credits_microservice.model.CreditType;
import nnt_data.credits_microservice.model.CustomerType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
/**
 * Servicio CreditOperationsService que implementa la interfaz CreditOperationsPort.
 * - createCredit: Crea un nuevo crédito utilizando la estrategia de creación correspondiente.
 * - updateCredit: Actualiza un crédito existente utilizando la estrategia de actualización correspondiente.
 * - getByCreditId: Recupera un crédito específico por su ID.
 * - getAllCredits: Recupera todos los créditos.
 * - deleteCredit: Elimina un crédito por su ID.
 * Utiliza mapas de estrategias de creación y actualización para manejar diferentes tipos de clientes.
 * Utiliza Mono y Flux de Reactor para manejar las operaciones de manera reactiva.
 * Dependencias:
 * - creationStrategies: Mapa de estrategias de creación de créditos por tipo de cliente.
 * - updateStrategies: Mapa de estrategias de actualización de créditos por tipo de cliente.
 * - creditRepository: Repositorio para operaciones de persistencia de créditos.
 * - creditMapper: Mapeador para convertir entre entidades y dominios de créditos.
 */
@Service
public class CreditOperationsService implements CreditOperationsPort {

    private final Map<CustomerType, CreditCreationStrategy> creationStrategies;
    private final Map<CustomerType, UpdateCreationStrategy> updateStrategies;
    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    @Override
    public Mono<CreditBase> createCredit(CreditBase credit) {
        return executeCreationStrategy(credit)
                .flatMap(this::saveAccount);
    }

    @Override
    public Mono<CreditBase> updateCredit(String creditId, CreditBase credit) {
        return Mono.just(credit)
                .flatMap(c -> creditRepository.findById(creditId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("No existe un crédito con el ID: " + c.getCreditId()))))
                .then(executeUpdateStrategy(credit))
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

    @Override
    public Mono<Void> deleteCredit(String creditId) {
        return creditRepository.findById(creditId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Crédito con id " + creditId + " no encontrado")))
                .flatMap(credit -> creditRepository.deleteById(creditId))
                .then();
    }

    @Override
    public Mono<Boolean> hasCreditCard(String customerId) {
        return creditRepository.findByCustomerId(customerId)
                .filter(credit -> credit.getType().equals(CreditType.CREDIT_CARD))
                .hasElements()
                .defaultIfEmpty(false);
    }

    public CreditOperationsService(Map<CustomerType, CreditCreationStrategy> creationStrategies,
                                    CreditRepository creditRepository,
                                    CreditMapper creditMapper,
                                   Map<CustomerType, UpdateCreationStrategy> updateStrategies) {
        this.creationStrategies = creationStrategies;
        this.updateStrategies = updateStrategies;
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

    private Mono<CreditBase> executeUpdateStrategy(CreditBase creditBase) {
        return Mono.just(creditBase)
                .filter(acc -> acc.getCustomerType() != null)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El tipo de cliente no puede ser null")))
                .flatMap(acc -> Mono.justOrEmpty(updateStrategies.get(acc.getCustomerType()))
                        .switchIfEmpty(Mono.error(
                                new IllegalArgumentException("Tipo de cliente no soportado: " + acc.getCustomerType())))
                        .flatMap(strategy -> strategy.updateCredit(acc)));
    }

    private Mono<CreditBase> saveAccount(CreditBase creditBase) {
        return Mono.just(creditBase)
                .flatMap(creditMapper::toEntity)
                .flatMap(creditRepository::save)
                .flatMap(creditMapper::toDomain);
    }



}
