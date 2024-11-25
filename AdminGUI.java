import java.awt.CardLayout;
import java.io.IOException;
import javax.swing.*;

public class AdminGUI {
    private static BankingSystem bankingSystem;
    private static JPanel cards;

    public AdminGUI() {
    }

    public static void main(String[] args) {
        bankingSystem = new BankingSystem();

        try {
            bankingSystem.readAccountsFromFile("accounts.csv");
            System.out.println("Accounts loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading accounts file: " + e.getMessage());
        }

        JFrame frame = new JFrame("Admin Banking System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cards = new JPanel(new CardLayout());
        frame.add(cards);
        
        JPanel loginPanel = new JPanel();
        placeLoginComponents(loginPanel);
        cards.add(loginPanel, "AdminLogin");
        
        JPanel adminPanel = new JPanel();
        placeAdminComponents(adminPanel);
        cards.add(adminPanel, "Admin");

        JPanel customerPanel = new JPanel();
        CustomerGUI.placeCustomerComponents(customerPanel); // Ensure method visibility is public in CustomerGUI
        cards.add(customerPanel, "Customer");

        frame.setVisible(true);
    }

    private static void placeLoginComponents(JPanel panel) {
        panel.setLayout(null);
        
        JLabel userLabel = new JLabel("User:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);
        
        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);
        
        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);
        
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 120, 25);
        panel.add(loginButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(150, 80, 100, 25); // Adjust the bounds as needed
        panel.add(backButton);
        
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            System.out.println("Admin attempting login for user: " + username);
            if (bankingSystem.authenticate(username, password)) {
                System.out.println("Admin login successful for user: " + username);
                JOptionPane.showMessageDialog(null, "Login successful!");
                CardLayout cl = (CardLayout) cards.getLayout();
                cl.show(cards, "Admin");
            } else {
                System.out.println("Admin login failed for user: " + username);
                JOptionPane.showMessageDialog(null, "Login failed!");
            }
        });

        backButton.addActionListener(e -> goBackToWelcome());
    }

    private static void goBackToWelcome() {
        JFrame frame = new JFrame("Banking System");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome to the Banking System");
        welcomeLabel.setBounds(10, 20, 380, 25);
        panel.add(welcomeLabel);

        JButton adminButton = new JButton("Admin Login");
        adminButton.setBounds(50, 80, 120, 25);
        panel.add(adminButton);

        JButton customerButton = new JButton("Customer Login");
        customerButton.setBounds(200, 80, 140, 25);
        panel.add(customerButton);

        adminButton.addActionListener(e -> {
            frame.dispose();
            AdminGUI.main(new String[0]);
        });

        customerButton.addActionListener(e -> {
            frame.dispose();
            CustomerGUI.main(new String[0]);
        });
    }

    public static void placeAdminComponents(JPanel panel) {
        panel.setLayout(null);
        
        JLabel adminLabel = new JLabel("Admin Portal");
        adminLabel.setBounds(10, 20, 160, 25);
        panel.add(adminLabel);
        
        JButton addCustomerButton = new JButton("Add Customer");
        addCustomerButton.setBounds(10, 60, 200, 25);
        panel.add(addCustomerButton);
        
        JButton viewCustomerButton = new JButton("View Customers");
        viewCustomerButton.setBounds(10, 100, 200, 25);
        panel.add(viewCustomerButton);
        
        JButton viewAccountDetailsButton = new JButton("View Account Details");
        viewAccountDetailsButton.setBounds(10, 140, 200, 25);
        panel.add(viewAccountDetailsButton);
        
        JButton deleteCustomerButton = new JButton("Delete Customer");
        deleteCustomerButton.setBounds(10, 180, 200, 25);
        panel.add(deleteCustomerButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 220, 200, 25);
        panel.add(backButton);
        
        addCustomerButton.addActionListener(e -> addCustomer());
        viewCustomerButton.addActionListener(e -> viewCustomers());
        viewAccountDetailsButton.addActionListener(e -> viewAccountDetails());
        deleteCustomerButton.addActionListener(e -> deleteCustomer());
        backButton.addActionListener(e -> goBackToWelcome());
    }

    private static void addCustomer() {
        String customerName = JOptionPane.showInputDialog("Enter customer name:");
        String initialDepositStr = JOptionPane.showInputDialog("Enter initial deposit amount:");
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");
        if (customerName != null && initialDepositStr != null && username != null && password != null) {
            try {
                double initialDeposit = Double.parseDouble(initialDepositStr);
                int accountNumber = bankingSystem.getAccounts().size() + 1003; // Generate numeric account number
                Account newAccount = new Account(String.valueOf(accountNumber), customerName, username, password, initialDeposit);
                bankingSystem.addAccount(newAccount);
                bankingSystem.getUsers().put(username, new User(username, password, Role.CUSTOMER)); // Add user to the banking system
                bankingSystem.saveAccountsToFile("accounts.csv"); // Save users to the same accounts file
                JOptionPane.showMessageDialog(null, "Customer added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid deposit amount.");
            }
        }
    }

    private static void viewCustomers() {
        StringBuilder customerList = new StringBuilder();
        for (Account account : bankingSystem.getAccounts()) {
            customerList.append("Name: ").append(account.getAccountHolderName())
                    .append(", Account Number: ").append(account.getAccountNumber())
                    .append(", Balance: ").append(account.getBalance()).append("\n");
        }
        JOptionPane.showMessageDialog(null, customerList.toString());
    }

    private static void viewAccountDetails() {
        String accountNumber = JOptionPane.showInputDialog("Enter account number to view details:");
        if (accountNumber != null) {
            Account account = bankingSystem.findAccount(accountNumber);
            if (account != null) {
                String accountDetails = "Account Number: " + account.getAccountNumber() +
                        "\nAccount Holder: " + account.getAccountHolderName() +
                        "\nUsername: " + account.getUsername() +
                        "\nBalance: " + account.getBalance();
                JOptionPane.showMessageDialog(null, accountDetails);
            } else {
                JOptionPane.showMessageDialog(null, "Account not found.");
            }
        }
    }

    private static void deleteCustomer() {
        String accountNumber = JOptionPane.showInputDialog("Enter account number to delete:");
        if (accountNumber != null) {
            int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this customer?");
            if (confirmation == JOptionPane.YES_OPTION) {
                bankingSystem.closeAccount(accountNumber);
                JOptionPane.showMessageDialog(null, "Customer deleted successfully!");
            }
        }
    }
}
