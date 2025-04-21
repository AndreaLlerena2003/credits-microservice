package nnt_data.credits_microservice.domain.utils;

import nnt_data.credits_microservice.model.Transaction;


public class BalanceCalculator {
    public static Double calculateDailyBalance(Double currentBalance, Transaction transaction, String creditId) {
        if(transaction.getType().equals(Transaction.TypeEnum.PAYMENT)){
            return currentBalance = (transaction.getAmount() - transaction.getAmount());
        } else if (transaction.getType().equals(Transaction.TypeEnum.SPENT)) {
            return currentBalance = (currentBalance + transaction.getAmount());
        }
        return currentBalance;
    }
}
