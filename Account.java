import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountNumber;
    private String accountHolderName;
    private String username;
    private String password;
    private double balance;
    private List<String> transactionHistory;

    public Account(String accountNumber, String accountHolderName, String username, String password, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
        System.out.println("Account created: " + accountNumber + " for " + accountHolderName); // Debugging statement
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add("Deposited: ₹" + amount + " | New Balance: ₹" + balance);
        System.out.println("Deposit recorded: ₹" + amount + " | New Balance: ₹" + balance); // Debugging statement
        System.out.println("Current Transaction History: " + transactionHistory); // Debugging statement
    }

    public void withdraw(double amount) {
        balance -= amount;
        transactionHistory.add("Withdrew: ₹" + amount + " | New Balance: ₹" + balance);
        System.out.println("Withdrawal recorded: ₹" + amount + " | New Balance: ₹" + balance); // Debugging statement
        System.out.println("Current Transaction History: " + transactionHistory); // Debugging statement
    }

    public void transfer(Account toAccount, double amount) {
        this.withdraw(amount);
        toAccount.deposit(amount);
        transactionHistory.add("Transferred: ₹" + amount + " to account: " + toAccount.getAccountNumber() + " | New Balance: ₹" + balance);
        System.out.println("Transfer recorded: ₹" + amount + " to account: " + toAccount.getAccountNumber() + " | New Balance: ₹" + balance); // Debugging statement
        System.out.println("Current Transaction History: " + transactionHistory); // Debugging statement
    }
}
