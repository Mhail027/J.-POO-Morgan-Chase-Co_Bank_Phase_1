package org.poo.bank.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.bank.account.Account;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private final List<Account> accounts = new LinkedList<Account>();

    /**
     * User receives access to an account
     *
     * @param account account's info
     */
    public void addAccount(final Account account) {
        if (account == null) {
            return;
        }

        if (!hasAccount(account.getIban())) {
            accounts.add(account);
        }
    }

    /**
     * Verify if the user have access to an account.
     *
     * @param iban account number
     * @return true, if it has access to account
     *         false, in contrary case
     */
    private boolean hasAccount(final String iban) {
        for (Account acct : accounts) {
            if (Objects.equals(acct.getIban(), iban)) {
                return true;
            }
        }
        return false;
    }
}
