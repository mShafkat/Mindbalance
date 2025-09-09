package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfessionalRegistrationPage extends JFrame {

    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JTextArea addressArea;
    private JComboBox<String> roleComboBox;
    private JButton registerBtn, backBtn;
    private String preselectedRole = null;

    public ProfessionalRegistrationPage() {
        initUI();
    }

    public ProfessionalRegistrationPage(String role) {
        this.preselectedRole = role.toLowerCase();
        initUI();
        roleComboBox.setSelectedItem(preselectedRole.substring(0, 1).toUpperCase() + preselectedRole.substring(1));
    }

    private void initUI() {
        setTitle("Professional Registration");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        JLabel bgLabel = new JLabel(new ImageIcon(getClass().getResource("/assets/psychiatrist_bg.jpg")));
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);
        add(bgLabel);
        
        // Fonts
        Font labelFont = new Font("Arial", Font.BOLD, 20);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.white);
        nameLabel.setFont(labelFont);
        nameLabel.setBounds(400, 120, 120, 30);
        bgLabel.add(nameLabel);

        nameField = new JTextField();
        nameField.setFont(fieldFont);
        nameField.setBounds(520, 120, 300, 40);
        bgLabel.add(nameField);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.white);
        emailLabel.setFont(labelFont);
        emailLabel.setBounds(400, 190, 120, 30);
        bgLabel.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(fieldFont);
        emailField.setBounds(520, 190, 300, 40);
        bgLabel.add(emailField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.white);
        passwordLabel.setFont(labelFont);
        passwordLabel.setBounds(400, 260, 120, 30);
        bgLabel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBounds(520, 260, 300, 40);
        bgLabel.add(passwordField);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.white);
        roleLabel.setFont(labelFont);
        roleLabel.setBounds(400, 330, 120, 30);
        bgLabel.add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"Psychologist", "Psychiatrist"});
        roleComboBox.setFont(fieldFont);
        roleComboBox.setBounds(520, 330, 300, 40);
        bgLabel.add(roleComboBox);

        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(Color.white);
        phoneLabel.setFont(labelFont);
        phoneLabel.setBounds(400, 400, 120, 30);
        bgLabel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setFont(fieldFont);
        phoneField.setBounds(520, 400, 300, 40);
        bgLabel.add(phoneField);

        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setForeground(Color.white);
        addressLabel.setFont(labelFont);
        addressLabel.setBounds(400, 470, 120, 30);
        bgLabel.add(addressLabel);

        addressArea = new JTextArea();
        addressArea.setFont(fieldFont);
        JScrollPane scrollPane = new JScrollPane(addressArea);
        scrollPane.setBounds(520, 470, 300, 80);
        bgLabel.add(scrollPane);

        // Buttons
        registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 20));
        registerBtn.setBounds(520, 580, 140, 50);
        bgLabel.add(registerBtn);

        backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 20));
        backBtn.setBounds(680, 580, 140, 50);
        bgLabel.add(backBtn);

        // Actions
        registerBtn.addActionListener(e -> registerProfessional());
        backBtn.addActionListener(e -> {
            dispose();
            new SignInRolePage().setVisible(true);
        });
    }

    private void registerProfessional() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO experts (expertsName, expertEmail, password, role, phone, expertAddress, status) "
                       + "VALUES (?, ?, ?, ?, ?, ?, 'pending')";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nameField.getText().trim());
            pst.setString(2, emailField.getText().trim());
            pst.setString(3, new String(passwordField.getPassword()).trim());
            pst.setString(4, roleComboBox.getSelectedItem().toString().toLowerCase());
            pst.setString(5, phoneField.getText().trim());
            pst.setString(6, addressArea.getText().trim());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Application sent to admin for verification.");
                dispose();
                new LoginPage().setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
