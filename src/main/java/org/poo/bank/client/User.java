package org.poo.bank.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.bank.DataBase;
import org.poo.bank.account.Account;
import org.poo.bank.transaction.Transaction;

import java.util.*;

import static org.poo.bank.Constants.*;

@Data
@NoArgsConstructor
public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private final List<Account> accounts = new LinkedList<Account>();
    ///  key = alias, value = iban
    private final Map<String, String> accountsByAlias = new HashMap<String, String>();
    private final List<Transaction> transactions = new LinkedList<Transaction>();

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
            addTransaction(new Transaction(NEW_ACCOUNT, DataBase.getTimestamp()));
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
        if (iban == null) {
            return false;
        }

        for (Account acct : accounts) {
            if (iban.equals(acct.getIban())) {
                return true;
            }
        }
        return false;
    }

    /**
     * The user lost access to an account.
     *
     * @param iban number of account
     */
    public void removeAccount(final String iban) {
        for (Account account : accounts) {
            if (account.getIban().equals(iban)) {
                accounts.remove(account);
                return;
            }
         }
    }

    /**
     * Add a new transaction in history of account.
     *
     * @param transaction details of transaction
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Associate a name to an account.
     *
     * @param alias name of account
     * @param iban number of account
     */
    public void setAlias(final String alias, final String iban) {
        accountsByAlias.put(alias, iban);
    }

    /**
     * Find the account which is associated with the given name.
     *
     * @param alias name of account
     * @return number of account
     */
    public String getAccountByAlias(final String alias) {
        return accountsByAlias.get(alias);
    }

    @JsonIgnore
    public Map<String, String> getAccountsByAlias() {
        return accountsByAlias;
    }

    @JsonIgnore
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
