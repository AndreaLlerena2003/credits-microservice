package nnt_data.credits_microservice.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import nnt_data.credits_microservice.api.ReportingApi;
import nnt_data.credits_microservice.domain.service.ReportingService;
import nnt_data.credits_microservice.model.PostSalarySummaryForPeriodRequest;
import nnt_data.credits_microservice.model.TransactionReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ReportingController implements ReportingApi {

    private final ReportingService reportingService;

    /**
     * GET /reporting/{cardNumber}/transactions : Obtiene los últimos 10 movimientos de una tarjeta de credito
     * Devuelve un reporte con las últimas 10 transacciones realizadas con la tarjeta de credito especificada
     *
     * @param cardNumber Número de la tarjeta de débito (required)
     * @param exchange
     * @return Reporte de transacciones generado exitosamente (status code 200)
     * or Número de tarjeta inválido o no encontrado (status code 400)
     * or Error interno del servidor (status code 500)
     */
    @Override
    public Mono<ResponseEntity<TransactionReport>> getLastTenTransactions(String cardNumber, ServerWebExchange exchange) {
        return reportingService.getLastTenTransactions(cardNumber)
                .map(transactionReport -> ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(transactionReport))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.status(500).build()));
    }



    /**
     * GET /reporting/salarySummary/{customerId} : Obtener el reporte de salarios promedios para un cliente
     *
     * @param customerId ID del usuario (required)
     * @param exchange
     * @return Resumen del usuario especificado (status code 200)
     */
    @Override
    public Mono<ResponseEntity<Map<String, Object>>> getSummarySalaryByCustomerId(String customerId, ServerWebExchange exchange) {
        return reportingService.generateResumeOfAvarageBalance(customerId)
                .collectList()
                .map(creditResumes -> {
                    Map<String, Object> response = Map.of(
                            "customerId", customerId,
                            "creditResumes", creditResumes
                    );
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorResponse = Map.of(
                            "error", e.getMessage()
                    );
                    return Mono.just(ResponseEntity.status(500).body(errorResponse));
                });
    }

    /**
     * POST /reporting/salarySummaryForPeriod : reporte
     *
     * @param postSalarySummaryForPeriodRequest (required)
     * @param exchange
     * @return Resumen del usuario especificado (status code 200)
     */
    @Override
    public Mono<ResponseEntity<Map<String, Object>>> postSalarySummaryForPeriod(Mono<PostSalarySummaryForPeriodRequest> postSalarySummaryForPeriodRequest, ServerWebExchange exchange) {
        return postSalarySummaryForPeriodRequest
                .flatMap(request -> reportingService.generateResumeOfAvarageBalanceForPeriod(request.getCreditId(), request.getStartDate(), request.getEndDate())
                        .map(creditResumes -> {
                            Map<String, Object> response = new HashMap<>();
                            response.put("CreditId", request.getCreditId());
                            response.put("CreditsResumes", creditResumes);
                            return ResponseEntity.ok(response);
                        })
                        .onErrorResume(e -> {
                            Map<String, Object> errorResponse = new HashMap<>();
                            errorResponse.put("error", e.getMessage());
                            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                        }));
    }
}
