package org.poo.bank.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.poo.bank.account.Account;
import org.poo.bank.card.Card;
import org.poo.bank.transaction.Transaction;

import java.util.List;

@Getter
public final class CommerciantReport {
    private final String commerciant;
    private double total;

    public CommerciantReport(final String commerciant, final double total) {
        this.commerciant = commerciant;
        this.total = total;
    }

    public void addAtTotal(final double amount) {
        total += amount;
    }

}
