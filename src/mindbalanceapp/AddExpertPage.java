package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddExpertPage extends JFrame {

    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JTextArea addressArea;
    private JComboBox<String> roleCombo, statusCombo;

    private final boolean isRegisteredUser;

    public AddExpertPage(boolean isRegisteredUser) {
        this.isRegisteredUser = isRegisteredUser;

        setTitle("Add Expert");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel bg = createBackgroundLabel();
        add(bg);

        JLabel title = new JLabel("Add Expert");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(50, 30, 400, 40);
        bg.add(title);

        // Form
        int labelX = 350, fieldX = 520, y = 140, h = 32, gap = 50, w = 300;

        JLabel nameLbl = label("Name:", labelX, y, bg);
        nameField = field(fieldX, y, w, h, bg);
        y += gap;

        JLabel emailLbl = label("Email:", labelX, y, bg);
        emailField = field(fieldX, y, w, h, bg);
        y += gap;

        JLabel passLbl = label("Password:", labelX, y, bg);
        passwordField = new JPasswordField();
        passwordField.setBounds(fieldX, y, w, h);
        bg.add(passwordField);
        y += gap;

        JLabel roleLbl = label("Role:", labelX, y, bg);
        roleCombo = new JComboBox<>(new String[]{"psychologist", "psychiatrist"});
        roleCombo.setBounds(fieldX, y, w, h);
        bg.add(roleCombo);
        y += gap;

        JLabel phoneLbl = label("Phone:", labelX, y, bg);
        phoneField = field(fieldX, y, w, h, bg);
        y += gap;

        JLabel addressLbl = label("Address:", labelX, y, bg);
        addressArea = new JTextArea();
        JScrollPane sp = new JScrollPane(addressArea);
        sp.setBounds(fieldX, y, w, 70);
        bg.add(sp);
        y += (gap + 25);

        JLabel statusLbl = label("Status:", labelX, y, bg);
        statusCombo = new JComboBox<>(new String[]{"active", "pending", "blocked"});
        statusCombo.setBounds(fieldX, y, w, h);
        bg.add(statusCombo);
        y += gap;

        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(fieldX, y, 120, 40);
        bg.add(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(fieldX + 140, y, 120, 40);
        bg.add(cancelBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(50, 700, 160, 35);
        bg.add(backBtn);

        // Actions
        saveBtn.addActionListener(e -> saveExpert());
        cancelBtn.addActionListener(e -> {
            dispose();
            new AdminManageExpertsPage(this.isRegisteredUser).setVisible(true);
        });
        backBtn.addActionListener(e -> {
            dispose();
            new AdminManageExpertsPage(this.isRegisteredUser).setVisible(true);
        });

        setVisible(true);
    }

    private JLabel createBackgroundLabel() {
        ImageIcon icon = null;
        try { icon = new ImageIcon(getClass().getResource("/assets/admin_users_bg.jpg")); } catch (Exception ignored) {}
        if (icon == null || icon.getIconWidth() <= 0) icon = new ImageIcon("assets/admin_users_bg.jpg");
        JLabel bg = new JLabel(icon);
        bg.setBounds(0, 0, 1200, 800);
        bg.setLayout(null);
        return bg;
    }

    private JLabel label(String text, int x, int y, JComponent parent) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setBounds(x, y, 150, 30);
        parent.add(l);
        return l;
    }

    private JTextField field(int x, int y, int w, int h, JComponent parent) {
        JTextField f = new JTextField();
        f.setBounds(x, y, w, h);
        parent.add(f);
        return f;
    }

    private void saveExpert() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        String addr = addressArea.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Email and Password are required.");
            return;
        }

        String sql = "INSERT INTO experts (expertsName, expertEmail, password, role, phone, expertAddress, status) VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setString(4, role);
            pst.setString(5, phone);
            pst.setString(6, addr);
            pst.setString(7, status);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Expert added successfully.");
                dispose();
                new AdminManageExpertsPage(this.isRegisteredUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Insert failed.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
