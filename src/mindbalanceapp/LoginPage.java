package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class LoginPage extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/login_bg.jpg"));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);

        // Fonts
        Font titleFont = new Font("SansSerif", Font.BOLD, 36);
        Font labelFont = new Font("SansSerif", Font.BOLD, 20);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 18);

        // Title
        JLabel title = new JLabel("Welcome Back!", SwingConstants.CENTER);
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        title.setBounds(400, 50, 400, 50);
        bgLabel.add(title);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(400, 180, 120, 30);
        bgLabel.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(fieldFont);
        emailField.setBounds(520, 180, 300, 45);
        bgLabel.add(emailField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(400, 260, 120, 30);
        bgLabel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBounds(520, 260, 300, 45);
        bgLabel.add(passwordField);

        // Buttons
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        loginBtn.setBounds(520, 340, 140, 50);
        loginBtn.addActionListener(e -> handleLogin());
        bgLabel.add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        registerBtn.setBounds(680, 340, 140, 50);
        registerBtn.addActionListener(e -> {
            dispose();
            new SignInRolePage().setVisible(true);
        });
        bgLabel.add(registerBtn);

        add(bgLabel);
        setVisible(true);
    }

    private void handleLogin() {
    String email = emailField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        return;
    }

    try (Connection con = DBConnection.getConnection()) {

        // 1️⃣ Check Admin table first
        String sqlAdmin = "SELECT admin_id, admin_email FROM admin WHERE admin_email=? AND password=?";
        PreparedStatement psAdmin = con.prepareStatement(sqlAdmin);
        psAdmin.setString(1, email);
        psAdmin.setString(2, password);
        ResultSet rsAdmin = psAdmin.executeQuery();

        if (rsAdmin.next()) {
            int adminId = rsAdmin.getInt("admin_id");
            SessionManager.setCurrentUser(adminId, "admin", email);
            dispose();
            new AdminPanel(true).setVisible(true);
            return; // stop further checking
        }

        // 2️⃣ Check Experts table
        String sqlExpert = "SELECT expert_id, expertsName, status FROM experts WHERE expertEmail=? AND password=?";
        PreparedStatement psExpert = con.prepareStatement(sqlExpert);
        psExpert.setString(1, email);
        psExpert.setString(2, password);
        ResultSet rsExpert = psExpert.executeQuery();

        if (rsExpert.next()) {
            int expertId = rsExpert.getInt("expert_id");
            String expertName = rsExpert.getString("expertsName"); // fetch name
            String status = rsExpert.getString("status");

            SessionManager.setCurrentUser(expertId, "expert", email, expertName); // pass name to session

            if ("active".equalsIgnoreCase(status)) {
                dispose();
                new HomePage().setVisible(true);
            } else if ("pending".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Your expert account is pending approval.");
            } else if ("blocked".equalsIgnoreCase(status)) {
               JOptionPane.showMessageDialog(this, "Your expert account has been blocked.");
            }
            return;
        }

        // 3️⃣ Users table
        String sqlUser = "SELECT user_id, name, role, status FROM users WHERE email=? AND password=?";
        PreparedStatement psUser = con.prepareStatement(sqlUser);
        psUser.setString(1, email);
        psUser.setString(2, password);
        ResultSet rsUser = psUser.executeQuery();

        if (rsUser.next()) {
            int userId = rsUser.getInt("user_id");
            String userName = rsUser.getString("name");
            String role = rsUser.getString("role");
            String status = rsUser.getString("status");

            SessionManager.setCurrentUser(userId, role, email, userName); 

            if ("active".equalsIgnoreCase(status)) {
                dispose();
                new HomePage().setVisible(true);
            } else if ("pending".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Your account is pending admin approval.");
            } else if ("blocked".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Your account has been blocked.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
    }
}

}
