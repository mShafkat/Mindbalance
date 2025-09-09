package mindbalanceapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Admin view for mood test results. Only accessible by admin users.
 */
public class AdminMoodTestResultsPage extends JFrame {

    public AdminMoodTestResultsPage(boolean isRegisteredUser) {
        // ✅ allow opening only if the current user is admin
        if (!SessionManager.isAdmin()) {
            JOptionPane.showMessageDialog(
                null,
                "Access denied. Admins only.",
                "Access Denied",
                JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        setTitle("Mood Test Results");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background image
        ImageIcon bgIcon = null;
        try {
            bgIcon = new ImageIcon(getClass().getResource("/assets/admin_moodresults_bg.jpg"));
        } catch (Exception ignored) {}
        if (bgIcon == null || bgIcon.getIconWidth() <= 0) {
            bgIcon = new ImageIcon("assets/admin_moodresults_bg.jpg"); // fallback
        }

        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1000, 700);
        bgLabel.setLayout(null);

        // Title
        JLabel title = new JLabel("All Mood Test Results", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.BLACK);
        title.setBounds(250, 30, 500, 50);
        bgLabel.add(title);

        // Table placeholder
        JTable resultTable = new JTable();
        resultTable.setFont(new Font("SansSerif", Font.PLAIN, 18));
        resultTable.setRowHeight(28);

        JTableHeader header = resultTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 20));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBounds(60, 100, 880, 460);
        bgLabel.add(scrollPane);

        // Back Button
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        backBtn.setBounds(30, 600, 140, 40);
        backBtn.setBackground(new Color(30, 144, 255));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        backBtn.addActionListener(e -> {
            dispose();
            new AdminPanel(true);
        });
        bgLabel.add(backBtn);

        add(bgLabel);

        // Load table data
        loadResults(resultTable);

        setVisible(true);
    }

    private void loadResults(JTable table) {
        String sql =
            "SELECT u.user_id, u.name, t.disorder_name, r.score, r.category, r.suggestion, r.taken_at " +
            "FROM mood_test_results r " +
            "JOIN users u ON r.user_id = u.user_id " +
            "JOIN mood_tests t ON r.test_id = t.test_id " +
            "ORDER BY r.taken_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"User ID", "User Name", "Disorder", "Score", "Category", "Suggestion", "Taken At"}, 0
            ) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("disorder_name"),
                    rs.getInt("score"),
                    rs.getString("category"),
                    rs.getString("suggestion"),
                    rs.getTimestamp("taken_at")
                });
            }

            table.setModel(model);
            if (table.getColumnCount() > 5) {
                table.getColumnModel().getColumn(5).setPreferredWidth(300); // Suggestion column
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "❌ Failed to load test results: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}