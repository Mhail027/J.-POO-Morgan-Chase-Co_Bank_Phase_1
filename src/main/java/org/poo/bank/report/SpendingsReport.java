package org.poo.bank.report;

import lombok.Getter;
import org.poo.bank.account.Account;
import org.poo.bank.transaction.Transaction;

import java.util.*;

@Getter
public final class SpendingsReport extends Report{
    private List<CommerciantReport> commerciants;

    public SpendingsReport(final Account account, final int startTimestamp, final int endTimestamp) {
        super(account, startTimestamp, endTimestamp);

        transactions = transactions.stream()
                               .filter(transaction -> transaction.getCommerciant() != null)
                               .toList();

        commerciants = findCommerciants().stream()
                               .sorted((r1, r2)
                                        -> r1.getCommerciant().compareTo(r2.getCommerciant())
                               ).toList();
    }

    private Collection<CommerciantReport> findCommerciants() {
        Map<String, CommerciantReport> commerciantsMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String commerciant = transaction.getCommerciant();
            double amount = transaction.getAmountAsDouble();

            if (commerciantsMap.containsKey(commerciant)) {
                CommerciantReport report = commerciantsMap.get(commerciant);
                report.addAtTotal(amount);
            } else {
                CommerciantReport report = new CommerciantReport(commerciant, amount);
                commerciantsMap.put(commerciant, report);
            }
        }

        return commerciantsMap.values();
    }

}
