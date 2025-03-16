import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;

public class Campus_feedback_system {
    private static Connection connection;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Campus_feedback_system::new);
    }

    public Campus_feedback_system() {
        // Initialize database connection
        initializeDatabaseConnection();
        showLoginScreen();
    }

    // Establish database connection
    private void initializeDatabaseConnection() {
        try {
            // Ensure you have the MySQL JDBC driver on your classpath
            String url = "jdbc:mysql://localhost:3306/login"; // Replace with your DB URL
            String username = "root"; // Replace with your DB username
            String password = ""; // Replace with your DB password
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to create an input field with styling
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return textField;
    }

    // Method to create a styled button
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 35));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(50, 150, 250));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }

    private void showLoginScreen() {
        JFrame frame = new JFrame("Login");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1; gbc.gridwidth = 1;
        frame.add(roleLabel, gbc);

        String[] roles = {"Admin", "Student"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        frame.add(roleBox, gbc);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 2;
        frame.add(userLabel, gbc);

        JTextField userField = createStyledTextField();
        gbc.gridx = 1;
        frame.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 3;
        frame.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(200, 30));
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        gbc.gridx = 1;
        frame.add(passField, gbc);

        JButton loginButton = createStyledButton("Login");
        gbc.gridx = 0; gbc.gridy = 4;
        frame.add(loginButton, gbc);

        JButton signupButton = createStyledButton("Sign Up");
        signupButton.setBackground(new Color(0, 180, 90));
        gbc.gridx = 1;
        frame.add(signupButton, gbc);

        // Login Action
        loginButton.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (role.equals("Admin")) {
                if (authenticateAdmin(username, password)) {
                    frame.dispose();
                    showAdminDashboard();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Admin Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (authenticateStudent(username, password)) {
                    frame.dispose();
                    showStudentProfile(username);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Student Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Redirect to Signup
        signupButton.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        frame.setVisible(true);
    }

    // Admin Authentication
    private boolean authenticateAdmin(String username, String password) {
        try {
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if a record is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Student Authentication
    private boolean authenticateStudent(String username, String password) {
        try {
            String query = "SELECT * FROM student WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if a record is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Admin Dashboard
    private void showAdminDashboard() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTextArea studentList = new JTextArea();
        studentList.setEditable(false);
        studentList.append("Registered Students:\n\n");

        try {
            String query = "SELECT * FROM student";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                studentList.append(resultSet.getString("name") + " | Age: " + resultSet.getInt("age") + " | Dept: " + resultSet.getString("department") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.add(new JScrollPane(studentList), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Student Profile Page
    private void showStudentProfile(String username) {
        JFrame frame = new JFrame("Student Profile");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        try {
            String query = "SELECT * FROM student WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String department = resultSet.getString("department");

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.anchor = GridBagConstraints.WEST;

                frame.add(new JLabel("Full Name: "), gbc);
                gbc.gridx = 1;
                frame.add(new JLabel(name), gbc);

                gbc.gridx = 0; gbc.gridy = 1;
                frame.add(new JLabel("Age: "), gbc);
                gbc.gridx = 1;
                frame.add(new JLabel(String.valueOf(age)), gbc);

                gbc.gridx = 0; gbc.gridy = 2;
                frame.add(new JLabel("Department: "), gbc);
                gbc.gridx = 1;
                frame.add(new JLabel(department), gbc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }
}