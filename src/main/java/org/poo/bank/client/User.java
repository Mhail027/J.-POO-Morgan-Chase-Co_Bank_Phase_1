package org.poo.bank.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;
import org.poo.bank.account.Account;
import org.poo.bank.transaction.Transaction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public final class User {
    private String firstName;
    private String lastName;
    private String email;

    private final List<Account> accounts;
    private final Map<String, String> accountsByAlias; //  key = alias, value = iban
    private final List<Transaction> transactions;

    public User() {
        accounts = new LinkedList<>();
        accountsByAlias = new HashMap<>();
        transactions = new LinkedList<>();
    }

    /**
     * User receives access to an account.
     *
     * @param account account's info
     * @throws IllegalArgumentException if user already has access to account
     */
    public void addAccount(@NonNull final Account account) throws IllegalArgumentException {
        if (hasAccount(account.getIban())) {
            throw new IllegalArgumentException("User has already access to account");
        }
        accounts.add(account);
    }

    /**
     * Verify if the user have access to an account.
     *
     * @param iban account number
     * @return true, if he has access to account
     *         false, if not
     */
    private boolean hasAccount(@NonNull final String iban) {
        return getAccount(iban) != null;
    }

    /**
     * @param iban account number OR alias
     * @return details of the account with the given iban
     */
    private Account getAccount(@NonNull final String iban) {
        return accounts.stream()
                      .filter(acct -> acct.getIban().equals(iban))
                      .findFirst().orElse(null);
    }

    /**
     * User lose access to an account.
     *
     * @param iban number of account
     */
    public void removeAccount(@NonNull final String iban) {
        accounts.removeIf(acct -> acct.getIban().equals(iban));
    }

    /**
     * Associate a name to an account.
     *
     * @param alias name of account
     * @param iban number of account
     */
    public void setAlias(@NonNull final String alias, @NonNull final String iban) {
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

    /**
     * Save the info of a transaction.
     *
     * @param transaction details of transaction
     */
    public void addTransaction(@NonNull final Transaction transaction) {
        transactions.add(transaction);
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
