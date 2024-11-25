import javax.swing.*;

public class SwingApp {
    public static void main(String[] args) {
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
}
