package nnt_data.credits_microservice.domain.service;

import lombok.RequiredArgsConstructor;
import nnt_data.credits_microservice.domain.utils.BalanceCalculator;
import nnt_data.credits_microservice.domain.utils.DateUtils;
import nnt_data.credits_microservice.infrastructure.persistence.entity.TransactionEntity;
import nnt_data.credits_microservice.infrastructure.persistence.mapper.TransactionMapper;
import nnt_data.credits_microservice.infrastructure.persistence.repository.CreditRepository;
import nnt_data.credits_microservice.infrastructure.persistence.repository.TransactionRepository;
import nnt_data.credits_microservice.model.CreditResume;
import nnt_data.credits_microservice.model.Transaction;
import nnt_data.credits_microservice.model.TransactionReport;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportingService {

    private final TransactionRepository transactionRepository;
    private final CreditRepository creditRepository;
    private final TransactionMapper transactionMapper;


    public Mono<CreditResume> generateResumeOfAvarageBalanceForPeriod(String creditId, Date startDate, Date endDate) {
        LocalDate startLocalDate = DateUtils.toLocalDate(startDate);
        LocalDate endLocalDate = DateUtils.toLocalDate(endDate);

        return creditRepository.findById(creditId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró la cuenta con ID: " + creditId)))
                .flatMap(credit ->
                        transactionRepository.findByCreditIdAndDateBetween(
                                        credit.getCreditId(),
                                        startDate,
                                        endDate
                                )
                                .collectList()
                                .map(transactions -> {
                                    Double averageBalance = calculateSalaryAverage(
                                            credit.getAmount(),
                                            transactions,
                                            startLocalDate,
                                            endLocalDate,
                                            credit.getCreditId()
                                    );
                                    return new CreditResume(credit.getCreditId(), credit.getType(), averageBalance);
                                })
                )
                .onErrorResume(e -> {
                    System.out.println("Error generating resume of average balance for account ID: " + e.getMessage());
                    return Mono.error(new IllegalArgumentException("Error al obtener el resumen de la cuenta: " + e.getMessage(), e));
                });
    }


    /**
     * Genera un resumen de saldo promedio de las cuentas de un cliente en el mes actual.
     *
     * @param customerId ID del cliente.
     * @return Un flujo de CreditResume que contiene el ID del crédito, el tipo y el saldo promedio.
     */
    public Flux<CreditResume> generateResumeOfAvarageBalance(String customerId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        return creditRepository.findByCustomerId(customerId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontraron cuentas para el cliente ID: " + customerId)))
                .flatMap(credit ->
                        transactionRepository.findByCreditIdAndDateBetween(
                                        credit.getCreditId(),
                                        DateUtils.toDate(startOfMonth),
                                        DateUtils.toDate(endOfMonth)
                                )
                                .collectList()
                                .map(transactions -> {
                                    Double averageBalance = calculateSalaryAverage(
                                            credit.getAmount(),
                                            transactions,
                                            startOfMonth,
                                            endOfMonth,
                                            credit.getCreditId()
                                    );
                                    return new CreditResume(credit.getCreditId(), credit.getType(),averageBalance);
                                })
                )
                .onErrorResume(e -> {
                    System.out.println("Error generating resume of average balance: " + e.getMessage());
                    return Mono.error(new IllegalArgumentException("Error al obtener las cuentas del cliente: " + e.getMessage(), e));
                });
    }


    private Double calculateSalaryAverage(Double initialBalance, List<TransactionEntity> transactions,
                                          LocalDate startOfMonth, LocalDate endOfMonth, String creditId) {

        Double dailyBalance = initialBalance;
        Double sumOfBalances = 0.0;
        LocalDate currentDate = startOfMonth;

        while (!currentDate.isAfter(endOfMonth)) {
            LocalDate finalCurrentDate = currentDate;
            List<TransactionEntity> dailyTransactions = transactions.stream()
                    .filter(transaction -> DateUtils.toLocalDate(transaction.getDate()).equals(finalCurrentDate))
                    .toList();

            for (TransactionEntity transactionEntity : dailyTransactions) {
                Mono<Transaction> transactionMono = transactionMapper.toDomain(transactionEntity);
                Transaction transaction = transactionMono.block();
                if (transaction != null) {
                    dailyBalance = BalanceCalculator.calculateDailyBalance(dailyBalance, transaction, creditId);
                }
            }
            sumOfBalances = sumOfBalances + dailyBalance;
            currentDate = currentDate.plusDays(1);
        }

        long daysInPeriod = ChronoUnit.DAYS.between(startOfMonth, endOfMonth) + 1;
        BigDecimal result = BigDecimal.valueOf(sumOfBalances)
                .divide(BigDecimal.valueOf(daysInPeriod), 2, RoundingMode.HALF_UP);

        return result.doubleValue();
    }


    public Mono<TransactionReport> getLastTenTransactions(String creditId) {
        return creditRepository.findByCreditId(creditId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "La tarjeta de credito con número " + creditId + " no existe")))
                .flatMap(creditCard -> {
                    return transactionRepository.findByCreditId(creditCard.getCreditId())
                            .sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                            .take(10)
                            .collectList()
                            .map(transactions -> {
                                TransactionReport report = new TransactionReport();
                                report.setCardNumber(creditCard.getCardNumber());
                                report.setCardNumber(creditCard.getCardNumber());
                                List<Transaction> transactionList = transactions.stream()
                                        .map(entity -> transactionMapper.toDomain(entity).block())
                                        .collect(Collectors.toList());
                                report.setTransactions(transactionList);
                                report.setTransactionCount(transactions.size());
                                report.setGenerationDate(new Date());
                                return report;
                            });
                });
    }




}
