package nnt_data.credit_service.infrastructure.persistence.mapper;

import nnt_data.credit_service.infrastructure.persistence.entity.TransactionEntity;
import nnt_data.credit_service.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TransactionMapperImplTest {

    @InjectMocks
    private TransactionMapperImpl transactionMapper;
    private Transaction transaction;
    private TransactionEntity transactionEntity;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setTransactionId("123");
        transaction.setAmount(10000.0);
        transaction.setType(Transaction.TypeEnum.PAYMENT);
        transaction.setCreditId("credit123");
        transaction.setDate(new Date());

        transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId("123");
        transactionEntity.setAmount(10000.0);
        transactionEntity.setType(Transaction.TypeEnum.PAYMENT);
        transactionEntity.setCreditId("credit123");
        transactionEntity.setDate(new Date());
    }

    @Test
    void toEntity_shouldMapTransactionToTransactionEntity(){
        Mono<TransactionEntity> result = transactionMapper.toEntity(transaction);
        // Then
        StepVerifier.create(result)
                .assertNext(entity -> {
                    assertEquals(transaction.getTransactionId(), entity.getTransactionId());
                    assertEquals(transaction.getAmount(), entity.getAmount());
                    assertEquals(transaction.getType(), entity.getType());
                    assertEquals(transaction.getCreditId(), entity.getCreditId());
                    assertEquals(transaction.getDate(), entity.getDate());
                    // Verifica aquí más propiedades según tu modelo
                })
                .verifyComplete();
    }

    @Test
    void toDomain_shouldMapTransactionEntityToTransaction() {
        // When
        Mono<Transaction> result = transactionMapper.toDomain(transactionEntity);

        // Then
        StepVerifier.create(result)
                .assertNext(domain -> {
                    assertEquals(transactionEntity.getTransactionId(), domain.getTransactionId());
                    assertEquals(transactionEntity.getAmount(), domain.getAmount());
                    assertEquals(transactionEntity.getType(), domain.getType());
                    assertEquals(transactionEntity.getCreditId(), domain.getCreditId());
                    assertEquals(transactionEntity.getDate(), domain.getDate());
                })
                .verifyComplete();
    }
}
