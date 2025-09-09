package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UpdateExpertPage extends JFrame {

    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JTextArea addressArea;
    private JComboBox<String> roleCombo, statusCombo;

    private final boolean isRegisteredUser;
    private final int userId;

    public UpdateExpertPage(boolean isRegisteredUser, int userId) {
        this.isRegisteredUser = isRegisteredUser;
        this.userId = userId;

        setTitle("Update Expert");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel bg = createBackgroundLabel();
        add(bg);

        JLabel title = new JLabel("Update Expert (ID: " + userId + ")");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(50, 30, 500, 40);
        bg.add(title);

        // Form
        int labelX = 350, fieldX = 520, y = 140, h = 32, gap = 50, w = 300;

        label("Name:", labelX, y, bg);
        nameField = field(fieldX, y, w, h, bg);
        y += gap;

        label("Email:", labelX, y, bg);
        emailField = field(fieldX, y, w, h, bg);
        y += gap;

        label("Password:", labelX, y, bg);
        passwordField = new JPasswordField();
        passwordField.setBounds(fieldX, y, w, h);
        bg.add(passwordField);
        y += gap;

        label("Role:", labelX, y, bg);
        roleCombo = new JComboBox<>(new String[]{"psychologist", "psychiatrist"});
        roleCombo.setBounds(fieldX, y, w, h);
        bg.add(roleCombo);
        y += gap;

        label("Phone:", labelX, y, bg);
        phoneField = field(fieldX, y, w, h, bg);
        y += gap;

        label("Address:", labelX, y, bg);
        addressArea = new JTextArea();
        JScrollPane sp = new JScrollPane(addressArea);
        sp.setBounds(fieldX, y, w, 70);
        bg.add(sp);
        y += (gap + 25);

        label("Status:", labelX, y, bg);
        statusCombo = new JComboBox<>(new String[]{"active", "pending", "blocked"});
        statusCombo.setBounds(fieldX, y, w, h);
        bg.add(statusCombo);
        y += gap;

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBounds(fieldX, y, 150, 40);
        bg.add(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(fieldX + 170, y, 120, 40);
        bg.add(cancelBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(50, 700, 160, 35);
        bg.add(backBtn);

        // Load current data
        loadExpert();

        // Actions
        saveBtn.addActionListener(e -> updateExpert());
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

    private void loadExpert() {
        String sql = "SELECT expertsName, expertEmail, password, role, phone, expertAddress, status FROM experts WHERE expert_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    nameField.setText(rs.getString("expertsName"));
                    emailField.setText(rs.getString("expertEmail"));
                    passwordField.setText(rs.getString("password"));
                    roleCombo.setSelectedItem(rs.getString("role"));
                    phoneField.setText(rs.getString("phone"));
                    addressArea.setText(rs.getString("expertAddress"));
                    statusCombo.setSelectedItem(rs.getString("status"));
                } else {
                    JOptionPane.showMessageDialog(this, "Expert not found.");
                    dispose();
                    new AdminManageExpertsPage(this.isRegisteredUser).setVisible(true);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load error: " + ex.getMessage());
        }
    }

    private void updateExpert() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        String addr = addressArea.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required.");
            return;
        }

        String sql = "UPDATE experts SET expertsName=?, expertEmail=?, password=?, role=?, phone=?, expertAddress=?, status=? WHERE expert_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setString(4, role);
            pst.setString(5, phone);
            pst.setString(6, addr);
            pst.setString(7, status);
            pst.setInt(8, userId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Expert updated successfully.");
                dispose();
                new AdminManageExpertsPage(this.isRegisteredUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
