package org.poo.bank.report;

import lombok.Getter;

@Getter
public final class CommerciantReport {
    private final String commerciant;
    private double total;

    public CommerciantReport(final String commerciant, final double total) {
        this.commerciant = commerciant;
        this.total = total;
    }

    /**
     * Increase the total sum of money.
     * @param amount amount with which is increased th total
     */
    public void addAtTotal(final double amount) {
        total += amount;
    }

}
