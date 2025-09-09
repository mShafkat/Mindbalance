package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.toedter.calendar.JDateChooser;

public class PatientRegistrationPage extends JFrame {

    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JTextArea addressArea;
    private JComboBox<String> genderCombo;
    private JButton registerBtn, backBtn;
    private JDateChooser dobChooser; // ✅ Using JDateChooser

    public PatientRegistrationPage() {
        setTitle("Patient Registration");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        JLabel bgLabel = new JLabel(new ImageIcon(getClass().getResource("/assets/patient_bg.jpg")));
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);
        add(bgLabel);

        // Fonts
        Font labelFont = new Font("Arial", Font.BOLD, 20);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.black);
        nameLabel.setFont(labelFont);
        nameLabel.setBounds(400, 100, 120, 30);
        bgLabel.add(nameLabel);

        nameField = new JTextField();
        nameField.setFont(fieldFont);
        nameField.setBounds(520, 100, 300, 40);
        bgLabel.add(nameField);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.black);
        emailLabel.setFont(labelFont);
        emailLabel.setBounds(400, 160, 120, 30);
        bgLabel.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(fieldFont);
        emailField.setBounds(520, 160, 300, 40);
        bgLabel.add(emailField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.black);
        passwordLabel.setFont(labelFont);
        passwordLabel.setBounds(400, 220, 120, 30);
        bgLabel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBounds(520, 220, 300, 40);
        bgLabel.add(passwordField);

        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(Color.black);
        phoneLabel.setFont(labelFont);
        phoneLabel.setBounds(400, 280, 120, 30);
        bgLabel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setFont(fieldFont);
        phoneField.setBounds(520, 280, 300, 40);
        bgLabel.add(phoneField);

        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setForeground(Color.black);
        addressLabel.setFont(labelFont);
        addressLabel.setBounds(400, 340, 120, 30);
        bgLabel.add(addressLabel);

        addressArea = new JTextArea();
        addressArea.setFont(fieldFont);
        JScrollPane scrollPane = new JScrollPane(addressArea);
        scrollPane.setBounds(520, 340, 300, 80);
        bgLabel.add(scrollPane);

        // DOB (Calendar Picker)
        JLabel dobLabel = new JLabel("DOB:");
        dobLabel.setForeground(Color.black);
        dobLabel.setFont(labelFont);
        dobLabel.setBounds(400, 440, 200, 30);
        bgLabel.add(dobLabel);

        dobChooser = new JDateChooser();
        dobChooser.setBounds(520, 440, 300, 40);
        dobChooser.setDateFormatString("yyyy-MM-dd"); // For SQL Date compatibility
        
        dobChooser.getJCalendar().getDayChooser().getDayPanel().setFont(new Font("Arial", Font.PLAIN, 18));
        dobChooser.getJCalendar().getMonthChooser().setFont(new Font("Arial", Font.PLAIN, 18));
        dobChooser.getJCalendar().getYearChooser().setFont(new Font("Arial", Font.PLAIN, 18));
        dobChooser.getDateEditor().getUiComponent().setFont(new Font("Arial", Font.PLAIN, 18)); 

        bgLabel.add(dobChooser);

        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setForeground(Color.black);
        genderLabel.setFont(labelFont);
        genderLabel.setBounds(400, 500, 120, 30);
        bgLabel.add(genderLabel);

        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(fieldFont);
        genderCombo.setBounds(520, 500, 300, 40);
        bgLabel.add(genderCombo);

        // Buttons
        registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 20));
        registerBtn.setBounds(520, 570, 140, 50);
        bgLabel.add(registerBtn);

        backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 20));
        backBtn.setBounds(680, 570, 140, 50);
        bgLabel.add(backBtn);

        // Actions
        registerBtn.addActionListener(e -> registerPatient());
        backBtn.addActionListener(e -> {
            dispose();
            new SignInRolePage().setVisible(true);
        });
    }

    private void registerPatient() {
        try (Connection conn = DBConnection.getConnection()) {
            java.util.Date selected = dobChooser.getDate();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid date of birth.");
                return;
            }

            Date sqlDate = new Date(selected.getTime()); // Convert to java.sql.Date

            String sql = "INSERT INTO users (name, email, password, role, phone, address, status, patientDOB, gender) "
                    + "VALUES (?, ?, ?, 'patient', ?, ?, 'active', ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nameField.getText().trim());
            pst.setString(2, emailField.getText().trim());
            pst.setString(3, new String(passwordField.getPassword()).trim());
            pst.setString(4, phoneField.getText().trim());
            pst.setString(5, addressArea.getText().trim());
            pst.setDate(6, sqlDate);
            pst.setString(7, (String) genderCombo.getSelectedItem());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");
                dispose();
                new LoginPage().setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
