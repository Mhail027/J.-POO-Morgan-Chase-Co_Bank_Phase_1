Name: Necula Mihail \
Group: 323CAa \
Year: 2024 - 2025

# Assignment POO - Project J. POO Morgan - Phase One

![](media/donald_duck_counting_money.gif.gif)

#### Assignment Link: [https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/2024/proiect-e1](https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/2024/proiect-e1)

## Structure

* src/main/java/org.poo/
    * bank/
      * account/ - abstract class for account \
        $~~~~~~~~~~~~~$ - 2 implementations for that class; one for every type of account: classic and savings \
        $~~~~~~~~~~~~~$ - account factory
      * card/ - abstract class for card \
        $~~~~~~~~$ - 2 implementation for that class; one for every type of card:classic and one time \
        $~~~~~~~~$ - card factory
      * client/ - contains just a class in which we save the info of a user
      * command/ - a class (CommandInput) which we use it to read data of a command from json files \
        $~~~~~~~~~~~~~~~~$ - an interface which is implemented by every command \
        $~~~~~~~~~~~~~~~~$ - command factory
      * currency/ - a class (Exchange) in which we save the exchange rate between 2 currencies \
        $~~~~~~~~~~~~~~$ - a class (CurrencyConvertor) which has the purpose of converting a sum \
        $~~~~~~~~~~~~~~$ of money from a currency to other; when we create the class, the given \
        $~~~~~~~~~~~~~~$ currencies are put in a graph and the exchange rates are the weights of \
        $~~~~~~~~~~~~~~$ the edges; using Floyd Warshall Algorithm we find all possible exchange \
        $~~~~~~~~~~~~~~$ rates between 2 currencies; all the exchanges are saved in a hashmap; \
        $~~~~~~~~~~~~~~$ when we want to find an exchange rate we just make an interrogation; \
        $~~~~~~~~~~~~~~$ this class is Singleton
      * database/ - a class (Database) which saves all users, accounts and cards of the bank; \
        $~~~~~~~~~~~~~~~~$ to do this, we use 3 hashmaps; this class is Singleton
      * generator/ - 2 generators: one for IBANs and the other for card number; every generator \
        $~~~~~~~~~~~~~~~~$ has a method which assure that the IBAN/card number is not already in \
        $~~~~~~~~~~~~~~~~$ use; these classes are Singletons
      * report/ - 2 classes for every report: simple and spendings one which extends the simple\
        $~~~~~~~~~~$ report \
        $~~~~~~~~~~$ - a class (ComerciantReport) which is an auxiliary one, being used just as \
        $~~~~~~~~~~$ a field in spending report
      * transaction/ - 2 classes: Transaction and TransactionBuilder
      * bank - contains a database, a currency convertor, an iban generator and a card number \
        $~~~~~~~~$ generator; this class is Singleton
    * checker/ - checker files
    * constants/ - string constants
    * graph/ - 3 classes (GraphNode, Edge, Graph); this package is used just by the currency \
      $~~~~~~~~~~$ convertor; graph class has a lot of missing basic functionalities because \
      $~~~~~~~~~~$ I did not need them in this project
    * input/ - 1 class (ObjectInput) which is used to put at common all data which are read \
      $~~~~~~~~~$ from json files
    * main/ - run the project from Main class
    * output/ - output formats
    * validator/ - validator of objects classes

## Commands

### AddCommand
-> create a new account using account factory; in constructor,  the account will be tied of \
his owner \
-> add the account in database \
-> add a successful transaction in account; when a transaction is added in account, is also added in data of \
the owner

### AddFundsCommand
-> add money in an account

### AddInterestCommand
-> add the interest for a savings account

### ChangeInterestRateCommand
-> change the interest rate of a savings account \
-> add a successful transaction in account (and to owner)

### CreateCardCommand
-> create a new card using card factory; in constructor, the card will be tied of an account \
-> add the card in database\
-> add a failed/successful transaction in the account (and to owner) with which we want to be \
associated the card

### CheckCardStatusCommand
-> if the account of the card is under or equal with the minimum balance, the card will be frozen \
and a transaction which announce this is added in account (and to owner)

### DeleteAccountCommand
-> remove the account from database \
-> delete all cards associate it with the account \
-> breaks the connection with the user (which is bidirectional) \
-> if account have remained funds, add a failed transaction in account (and to owner)

### DeleteCardCommand
-> remove the card from database \
-> breaks the connection with the account (which is bidirectional) \
-> add a successful transaction in account (and to owner)

### SetMinimumBalanceCommand
-> set the minimum balance of an account

### PayOnlineCommand
-> a card pay to a commerciant => take money out from account \
-> add a failed/successful transaction to account (and to owner) 

### SendMoneyCommand
-> take money from an account, make a currency conversion and put them in other account \
-> add a failed transaction to sender (account and owner) or a successful transaction to sender \
and receiver

### SplitMoneyCommand
-> more accounts split the bill between them 50-50 \
-> make currency conversion, and take from every account an equal sum of money \
-> add a failed/successful transaction to every account from split (and their owner)

### SetAliasCommand
-> a user set an alias to an account; this alias can be used in the command of send money \
as receiver \
-> every user has a hashmap, where key = alias and value = iban of account

### GetReportCommand
-> print all transaction from an account during a period

### GetSpendingsReportCommand
-> print all transaction of type "PayOnline" from an account during a period

### PrintUsersCommand
-> print all users at output

### PrintTransactionsCommand
-> print transactions of a user at output














