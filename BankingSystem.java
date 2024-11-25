import java.io.*;
import java.util.*;

public class BankingSystem {
    private final List<Account> accounts;
    private final Map<String, User> users;
    private User loggedInUser; // Track the currently logged-in user

    public BankingSystem() {
        this.accounts = new ArrayList<>();
        this.users = new HashMap<>();
        // Initialize admin credentials
        users.put("admin", new User("admin", "password123", Role.ADMIN));
        // Add some default customer for testing purposes
        users.put("customer1", new User("customer1", "password123", Role.CUSTOMER));
    }

    public boolean authenticate(String username, String password) {
        // Check the users map first
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user;
            System.out.println("Authentication successful for user: " + username); // Debugging statement
            return true;
        }

        // Check the accounts list for customer login
        for (Account account : accounts) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                loggedInUser = new User(username, password, Role.CUSTOMER); // Set the logged-in user
                System.out.println("Authentication successful for user: " + username); // Debugging statement
                return true;
            }
        }
        System.out.println("Authentication failed for user: " + username); // Debugging statement
        return false;
    }

    public void readAccountsFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true; // Skip the header line
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length != 5) { // Adjusted for the correct number of parts
                    System.out.println("Skipping line due to invalid format: " + line);
                    continue;
                }
                String accountNumber = parts[0].trim();
                String accountHolderName = parts[1].trim();
                String username = parts[2].trim();
                String password = parts[3].trim();
                double balance;
                try {
                    balance = Double.parseDouble(parts[4].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line due to invalid balance format: " + line);
                    continue;
                }
                Account account = new Account(accountNumber, accountHolderName, username, password, balance);
                accounts.add(account);
                users.put(username, new User(username, password, Role.CUSTOMER));
                System.out.println("Loaded account: " + accountNumber + ", Holder: " + accountHolderName + ", Username: " + username + ", Balance: " + balance);
            }
        }
        System.out.println("Accounts loaded from file."); // Debugging statement
    }

    public void saveAccountsToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("accountNumber,accountHolderName,username,password,balance\n"); // CSV header
            for (Account account : accounts) {
                writer.write(account.getAccountNumber() + "," +
                             account.getAccountHolderName() + "," +
                             account.getUsername() + "," +
                             account.getPassword() + "," +
                             account.getBalance() + "\n");
            }
            System.out.println("Accounts saved to file."); // Debugging statement
        } catch (IOException e) {
            System.out.println("Error saving accounts file: " + e.getMessage());
        }
    }

    public void listAllAccounts() {
        for (Account account : accounts) {
            System.out.println(account.getAccountNumber() + " | " + account.getAccountHolderName() + " | " + account.getBalance());
        }
    }

    public void addAccount(Account account) {
        accounts.add(account);
        saveAccountsToFile("accounts.csv");
    }

    public void closeAccount(String accountNumber) {
        if (loggedInUser.getRole() == Role.ADMIN) {
            Account account = findAccount(accountNumber);
            if (account != null) {
                accounts.remove(account);
                System.out.println("Closed account " + accountNumber);
                saveAccountsToFile("accounts.csv");
            } else {
                System.out.println("Account not found");
            }
        } else {
            System.out.println("Unauthorized: Only admin can close accounts");
        }
    }

    public void updateAccount(String accountNumber, String newAccountHolderName) {
        if (loggedInUser.getRole() == Role.ADMIN) {
            Account account = findAccount(accountNumber);
            if (account != null) {
                account.setAccountHolderName(newAccountHolderName);
                System.out.println("Updated account holder name for account " + accountNumber);
                saveAccountsToFile("accounts.csv");
            } else {
                System.out.println("Account not found");
            }
        } else {
            System.out.println("Unauthorized: Only admin can update account holder names");
        }
    }

    public void deposit(String accountNumber, double amount, String username, String password) {
        Account account = findAccount(accountNumber);
        if (account != null && authenticate(username, password)) {
            if (amount > 0) {
                account.deposit(amount);
                System.out.println("Deposited " + amount + " to account " + accountNumber);
                saveAccountsToFile("accounts.csv");
            } else {
                System.out.println("Deposit amount must be positive.");
            }
        } else {
            System.out.println("Unauthorized or account not found.");
        }
    }

    public void withdraw(String accountNumber, double amount, String username, String password) {
        Account account = findAccount(accountNumber);
        if (account != null && authenticate(username, password) && canAccessAccount(account)) {
            if (amount > 0 && account.getBalance() >= amount) {
                account.withdraw(amount);
                System.out.println("Withdrew " + amount + " from account " + accountNumber);
                saveAccountsToFile("accounts.csv");
            } else if (amount <= 0) {
                System.out.println("Withdrawal amount must be positive.");
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Unauthorized or account not found.");
        }
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, double amount, String username, String password) {
        Account fromAccount = findAccount(fromAccountNumber);
        Account toAccount = findAccount(toAccountNumber);
        if (fromAccount != null && toAccount != null && authenticate(username, password) && canAccessAccount(fromAccount)) {
            if (amount > 0 && fromAccount.getBalance() >= amount) {
                fromAccount.transfer(toAccount, amount);
                System.out.println("Transferred " + amount + " from account " + fromAccountNumber + " to account " + toAccountNumber);
                saveAccountsToFile("accounts.csv");
            } else if (amount <= 0) {
                System.out.println("Transfer amount must be positive.");
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Unauthorized or account not found.");
        }
    }

    public void changePassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            user.setPassword(newPassword);
            // Update password in Account
            for (Account account : accounts) {
                if (account.getUsername().equals(username)) {
                    account.setPassword(newPassword);
                    break;
                }
            }
            System.out.println("Password updated for user: " + username);
            saveAccountsToFile("accounts.csv"); // Ensure changes are saved
        }
    }

    public Account findAccount(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public boolean canAccessAccount(Account account) {
        return account.getUsername().equals(loggedInUser.getUsername());
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public String getTransactionHistory(String accountNumber) {
        System.out.println("Attempting to fetch transaction history for account: " + accountNumber); // Debugging statement
        Account account = findAccount(accountNumber);
        if (account != null) {
            StringBuilder history = new StringBuilder("Transaction History for account: " + accountNumber + "\n");
            for (String transaction : account.getTransactionHistory()) {
                history.append(transaction).append("\n");
            }
            System.out.println("Transaction history fetched for account: " + accountNumber + "\n" + history); // Debugging statement
            return history.toString();
        } else {
            return "Account not found.";
        }
    }

    public static void main(String[] args) {
        BankingSystem bankingSystem = new BankingSystem();
    
        // Add a simple login check
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();
    
            if (!bankingSystem.authenticate(username, password)) {
                System.out.println("Authentication failed. Exiting.");
                return;
            }
        }
    
        // Load accounts from file
        try {
            bankingSystem.readAccountsFromFile("accounts.csv");
            System.out.println("Accounts loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading accounts file: " + e.getMessage());
            return;
        }
    
        // List all accounts
        bankingSystem.listAllAccounts();
    
        // Example operations
        bankingSystem.updateAccount("1001", "New John Doe");
        bankingSystem.closeAccount("1002");
        bankingSystem.deposit("1001", 1000.00, "customer1", "password123");
        bankingSystem.withdraw("1001", 500.00, "customer1", "password123");
        bankingSystem.transfer("1001", "1002", 500.00, "customer1", "password123");
    
        // Print transaction history for account 1001
        System.out.println(bankingSystem.getTransactionHistory("1001"));
    }
}
