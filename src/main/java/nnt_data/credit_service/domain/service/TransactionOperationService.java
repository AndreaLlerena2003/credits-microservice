package nnt_data.credit_service.domain.service;

import lombok.RequiredArgsConstructor;
import nnt_data.credit_service.application.port.TransactionOperationsPort;
import nnt_data.credit_service.domain.validator.ValidatorFactory;
import nnt_data.credit_service.infrastructure.persistence.mapper.TransactionMapper;
import nnt_data.credit_service.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credit_service.infrastructure.persistence.repository.TransactionRepository;
import nnt_data.credit_service.model.Transaction;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TransactionOperationService implements TransactionOperationsPort {

    private final TransactionMapper transactionMapper;
    private final ValidatorFactory validatorFactory;
    private final TransactionRepository transactionRepository;
    private final CreditRepository creditRepository;

    @Override
    public Mono<Transaction> createTransaction(Transaction transaction) {
        transaction.setDate(new Date());
        return creditRepository.findById(transaction.getCreditId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Credit not found")))
                .flatMap(credit -> validatorFactory.getTransactionValidator(credit)
                        .validate(transaction)
                        .onErrorMap(e -> new IllegalArgumentException(e.getMessage())))
                .flatMap(transactionMapper::toEntity)
                .flatMap(transactionRepository::save)
                .flatMap(transactionMapper::toDomain);
    }

    @Override
    public Flux<Transaction> getTransactions() {
        return transactionRepository.findAll()
                .flatMap(transactionMapper::toDomain);
    }

    @Override
    public Flux<Transaction> getTransactionByCreditId(String creditId) {
        return transactionRepository.findByCreditId(creditId)
                .flatMap(entity -> transactionMapper.toDomain(entity))
                .switchIfEmpty(Flux.empty())
                .onErrorResume(error -> Flux.error(new RuntimeException("Error al obtener las transacciones por ID del credito")));
    }

}
