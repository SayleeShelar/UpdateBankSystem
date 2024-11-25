import java.awt.CardLayout;
import java.io.IOException;
import javax.swing.*;

public class CustomerGUI {
    private static BankingSystem bankingSystem;
    private static JPanel cards;

    public static void main(String[] args) {
        bankingSystem = new BankingSystem();

        // Attempt to read accounts from the CSV file
        try {
            bankingSystem.readAccountsFromFile("accounts.csv");
            System.out.println("Accounts loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading accounts file: " + e.getMessage());
        }

        JFrame frame = new JFrame("Customer Banking System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cards = new JPanel(new CardLayout());
        frame.add(cards);

        JPanel loginPanel = new JPanel();
        placeLoginComponents(loginPanel);
        cards.add(loginPanel, "CustomerLogin");

        JPanel customerPanel = new JPanel();
        placeCustomerComponents(customerPanel);
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

        loginButton.addActionListener(event -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            System.out.println("Customer attempting login for user: " + username); // Debugging statement
            if (bankingSystem.authenticate(username, password)) {
                System.out.println("Customer login successful for user: " + username); // Debugging statement
                JOptionPane.showMessageDialog(null, "Login successful!");
                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, "Customer");
            } else {
                System.out.println("Customer login failed for user: " + username); // Debugging statement
                JOptionPane.showMessageDialog(null, "Login failed!");
            }
        });

        backButton.addActionListener(event -> goBackToWelcome());
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

    public static void placeCustomerComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel customerLabel = new JLabel("Customer Portal");
        customerLabel.setBounds(10, 20, 160, 25);
        panel.add(customerLabel);

        JButton viewBalanceButton = new JButton("View Balance");
        viewBalanceButton.setBounds(10, 60, 150, 25);
        panel.add(viewBalanceButton);

        JButton transferMoneyButton = new JButton("Transfer Money");
        transferMoneyButton.setBounds(10, 100, 150, 25);
        panel.add(transferMoneyButton);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setBounds(10, 140, 150, 25);
        panel.add(changePasswordButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 180, 150, 25);
        panel.add(backButton);

        viewBalanceButton.addActionListener(event -> viewBalance());
        transferMoneyButton.addActionListener(event -> transferMoney());
        changePasswordButton.addActionListener(event -> changePassword());
        backButton.addActionListener(event -> goBackToLogin());
    }

    private static void viewBalance() {
        String accountNumber = JOptionPane.showInputDialog("Enter your account number:");
        String username = JOptionPane.showInputDialog("Enter your username:");
        JPasswordField passwordField = new JPasswordField();
        JOptionPane.showConfirmDialog(null, passwordField, "Enter your password:", JOptionPane.OK_CANCEL_OPTION);
        String password = new String(passwordField.getPassword());
        if (bankingSystem.authenticate(username, password)) {
            Account account = bankingSystem.findAccount(accountNumber);
            if (account != null) {
                JOptionPane.showMessageDialog(null, "Current Balance: " + account.getBalance());
            } else {
                JOptionPane.showMessageDialog(null, "Account not found.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Authentication failed.");
        }
    }

    private static void transferMoney() {
        String fromAccountNumber = JOptionPane.showInputDialog("Enter your account number:");
        String toAccountNumber = JOptionPane.showInputDialog("Enter the recipient's account number:");
        String amountStr = JOptionPane.showInputDialog("Enter the amount to transfer:");
        String username = JOptionPane.showInputDialog("Enter your username:");
        JPasswordField passwordField = new JPasswordField();
        JOptionPane.showConfirmDialog(null, passwordField, "Enter your password:", JOptionPane.OK_CANCEL_OPTION);
        String password = new String(passwordField.getPassword());
        if (bankingSystem.authenticate(username, password)) {
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                bankingSystem.transfer(fromAccountNumber, toAccountNumber, amount, username, password);
                JOptionPane.showMessageDialog(null, "Transfer successful!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid amount.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Authentication failed.");
        }
    }

    private static void changePassword() {
        String username = JOptionPane.showInputDialog("Enter your username:");
        JPasswordField oldPasswordField = new JPasswordField();
        JOptionPane.showConfirmDialog(null, oldPasswordField, "Enter your old password:", JOptionPane.OK_CANCEL_OPTION);
        String oldPassword = new String(oldPasswordField.getPassword());
        if (bankingSystem.authenticate(username, oldPassword)) {
            JPasswordField newPasswordField = new JPasswordField();
            JOptionPane.showConfirmDialog(null, newPasswordField, "Enter your new password:", JOptionPane.OK_CANCEL_OPTION);
            String newPassword = new String(newPasswordField.getPassword());
            bankingSystem.changePassword(username, newPassword); // Call changePassword method to save the changes
            JOptionPane.showMessageDialog(null, "Password changed successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Authentication failed.");
        }
    }

    private static void goBackToLogin() {
        JFrame frame = new JFrame("Banking System");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame);

        frame.setVisible(true);
    }
}
