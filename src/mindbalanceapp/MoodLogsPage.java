package mindbalanceapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class MoodLogsPage extends JFrame {

    private final int userId;
    private final boolean isRegisteredUser;
    private JTable table;

    public MoodLogsPage(boolean isRegisteredUser, int loggedInPatientId) {
        this.isRegisteredUser = isRegisteredUser;
        this.userId = loggedInPatientId;
        initUI();
        loadLogs();
    }

    private void initUI() {
        setTitle("Mood Logs");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        JPanel bg = new JPanel() {
            ImageIcon icon = new ImageIcon(getClass().getResource("/assets/admin_users_bg.jpg"));
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (icon != null) g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        bg.setLayout(null);
        bg.setBounds(0,0,1000,700);
        add(bg);

        JLabel title = new JLabel("My Mood Logs", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBounds(300, 20, 400, 40);
        bg.add(title);

        table = new JTable();
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 90, 920, 480);
        bg.add(sp);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(30, 590, 120, 36);
        bg.add(btnRefresh);
        btnRefresh.addActionListener(e -> loadLogs());

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(160, 590, 120, 36);
        btnBack.addActionListener(e -> dispose());
        bg.add(btnBack);

        setVisible(true);
    }

    private void loadLogs() {
        if (userId <= 0) {
            JOptionPane.showMessageDialog(this, "You must be logged in to view mood logs.", "Not logged in", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        String sql = "SELECT id, mood, intensity, note, log_date, created_at FROM mood_logs WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Mood","Intensity","Note","Log Date","Created At"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("mood"),
                    rs.getInt("intensity"),
                    rs.getString("note"),
                    rs.getDate("log_date"),
                    rs.getTimestamp("created_at")
                });
            }
            table.setModel(model);
            if (table.getColumnCount() > 3) table.getColumnModel().getColumn(3).setPreferredWidth(400);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load logs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
