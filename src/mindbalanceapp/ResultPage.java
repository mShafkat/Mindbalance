package mindbalanceapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ResultPage extends JFrame {

    public ResultPage(String disorder, int score, String category, String suggestion, boolean isRegisteredUser) {
        setTitle("Test Result - " + disorder);
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/result_bg.jpg"));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1000, 700);
        bgLabel.setLayout(null);

        // Score Label
        JLabel scoreLabel = new JLabel("Your Total Score: " + score);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(100, 100, 600, 40);
        bgLabel.add(scoreLabel);

        // Category Label
        JLabel categoryLabel = new JLabel("Result Category: " + category);
        categoryLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setBounds(100, 160, 600, 40);
        bgLabel.add(categoryLabel);

        // Suggestion Label
        JLabel suggestionLabel = new JLabel("Suggestion: " + suggestion);
        suggestionLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        suggestionLabel.setForeground(Color.WHITE);
        suggestionLabel.setBounds(100, 220, 800, 40);
        bgLabel.add(suggestionLabel);

        // Expert Profile Button
        JButton expertBtn = new JButton("Expert Profile");
        expertBtn.setBounds(100, 320, 180, 40);
        expertBtn.setBackground(Color.WHITE);
        expertBtn.setForeground(Color.BLACK);
        expertBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        expertBtn.addActionListener(e -> {
            dispose();
            new ExpertProfileSelection(isRegisteredUser);
        });
        bgLabel.add(expertBtn);

        // Homepage Button
        JButton homeBtn = new JButton("Homepage");
        homeBtn.setBounds(300, 320, 180, 40);
        homeBtn.setBackground(Color.WHITE);
        homeBtn.setForeground(Color.BLACK);
        homeBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        homeBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });
        bgLabel.add(homeBtn);

        // (Optional) Back to Test button
        JButton retakeBtn = new JButton("Retake Test");
        retakeBtn.setBounds(500, 320, 180, 40);
        retakeBtn.setBackground(Color.WHITE);
        retakeBtn.setForeground(Color.BLACK);
        retakeBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        retakeBtn.addActionListener(e -> {
            dispose();
            new DisorderSelectionPage();
        });
        bgLabel.add(retakeBtn);

        add(bgLabel);
        setVisible(true);
    }

    /**
     * List all mood_test_results for the logged-in user
     * Usage: new ResultPage(true);
     */
    public ResultPage(boolean listForCurrentUser) {
        if (!listForCurrentUser) throw new IllegalArgumentException("Pass true to open the current user's results list.");
        int userId = SessionManager.getCurrentUserId();
        if (userId <= 0) {
            JOptionPane.showMessageDialog(this, "You must be logged in to view your test history.", "Not logged in", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setTitle("My Mood Test Results");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTable table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        JButton btnClose = new JButton("Close");
        bottom.add(btnRefresh);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadResultsInto(table, userId));
        btnClose.addActionListener(e -> dispose());

        loadResultsInto(table, userId);
        setVisible(true);
    }

    private void loadResultsInto(JTable table, int userId) {
        String sql = "SELECT r.result_id, t.disorder_name, r.score, r.category, r.suggestion, r.taken_at " +
                "FROM mood_test_results r JOIN mood_tests t ON r.test_id = t.test_id " +
                "WHERE r.user_id = ? ORDER BY r.taken_at DESC";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel m = new DefaultTableModel(
                    new Object[]{"Result ID", "Test (Disorder)", "Score", "Category", "Suggestion", "Taken At"}, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
            while (rs.next()) {
                m.addRow(new Object[]{
                        rs.getInt("result_id"),
                        rs.getString("disorder_name"),
                        rs.getInt("score"),
                        rs.getString("category"),
                        rs.getString("suggestion"),
                        rs.getTimestamp("taken_at")
                });
            }
            table.setModel(m);
            if (table.getColumnCount() > 4) table.getColumnModel().getColumn(4).setPreferredWidth(380);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load results: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
