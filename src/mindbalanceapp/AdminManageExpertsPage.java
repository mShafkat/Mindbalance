package mindbalanceapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.JTableHeader;


public class AdminManageExpertsPage extends JFrame {

    private JTable expertsTable;
    private DefaultTableModel tableModel;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn, backBtn, homeBtn;

    private final boolean isRegisteredUser;

    public AdminManageExpertsPage(boolean isRegisteredUser) {
        this.isRegisteredUser = isRegisteredUser;

        setTitle("Manage Expert Profiles");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        JLabel bgLabel = createBackgroundLabel();
        add(bgLabel);

        // Title
        JLabel title = new JLabel("Expert Profiles");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(50, 25, 600, 50);
        bgLabel.add(title);

        /// Table
        String[] cols = {"ID", "Name", "Email", "Role", "Phone", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        expertsTable = new JTable(tableModel);
        expertsTable.setRowHeight(28);
        expertsTable.setFont(new Font("SansSerif", Font.PLAIN, 16));

        // Make table header bold
        JTableHeader header = expertsTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Shorten table height so buttons fit
        JScrollPane sp = new JScrollPane(expertsTable);
        sp.setBounds(50, 100, 1100, 450);
        bgLabel.add(sp);

        // Buttons
        Font btnFont = new Font("SansSerif", Font.BOLD, 16);

        addBtn = new JButton("➕ Add");
        addBtn.setFont(btnFont);
        addBtn.setBounds(50, 570, 110, 40);
        bgLabel.add(addBtn);

        updateBtn = new JButton("✏ Update");
        updateBtn.setFont(btnFont);
        updateBtn.setBounds(170, 570, 110, 40);
        bgLabel.add(updateBtn);

        deleteBtn = new JButton("🗑 Delete");
        deleteBtn.setFont(btnFont);
        deleteBtn.setBounds(290, 570, 110, 40);
        bgLabel.add(deleteBtn);

        refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(btnFont);
        refreshBtn.setBounds(410, 570, 120, 40);
        bgLabel.add(refreshBtn);

        backBtn = new JButton("⬅ Back to Admin");
        backBtn.setFont(btnFont);
        backBtn.setBounds(50, 630, 220, 40);
        bgLabel.add(backBtn);

        homeBtn = new JButton("🏠 Homepage");
        homeBtn.setFont(btnFont);
        homeBtn.setBounds(1000, 630, 140, 35);
        bgLabel.add(homeBtn);

        // Actions
        addBtn.addActionListener(e -> {
            dispose();
            new AddExpertPage(this.isRegisteredUser).setVisible(true);
        });

        updateBtn.addActionListener(e -> {
            int row = expertsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an expert to update.");
                return;
            }
            int expert_id = Integer.parseInt(expertsTable.getValueAt(row, 0).toString());
            dispose();
            new UpdateExpertPage(this.isRegisteredUser, expert_id).setVisible(true);
        });

        deleteBtn.addActionListener(e -> deleteSelectedExpert());
        refreshBtn.addActionListener(e -> loadExperts());

        backBtn.addActionListener(e -> {
            dispose();
            new AdminPanel(this.isRegisteredUser);
        });

        homeBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        // Initial load
        loadExperts();

        setVisible(true);
    }

    private JLabel createBackgroundLabel() {
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(getClass().getResource("/assets/admin_users_bg.jpg"));
        } catch (Exception ignored) {}
        if (icon == null || icon.getIconWidth() <= 0) {
            // Fallback if resource path differs on your setup
            icon = new ImageIcon("assets/admin_users_bg.jpg");
        }
        JLabel bg = new JLabel(icon);
        bg.setBounds(0, 0, 1200, 800);
        bg.setLayout(null);
        return bg;
    }

    private void loadExperts() {
        tableModel.setRowCount(0);
        String sql = "SELECT expert_id, expertsName, expertEmail, role, phone, status FROM experts " +
                     "WHERE role IN ('psychologist', 'psychiatrist') ORDER BY expert_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("expert_id"));
                row.add(rs.getString("expertsName"));
                row.add(rs.getString("expertEmail"));
                row.add(rs.getString("role"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("status"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Failed to load experts: " + ex.getMessage());
        }
    }

    private void deleteSelectedExpert() {
        int row = expertsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Please select an expert to delete.");
            return;
        }
        int expert_id = Integer.parseInt(expertsTable.getValueAt(row, 0).toString());
        String name = expertsTable.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "🗑 Delete expert \"" + name + "\" (ID " + expert_id + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM experts WHERE expert_id = ? AND role IN ('psychologist','psychiatrist')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, expert_id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Expert deleted.");
                loadExperts();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Delete failed. Expert not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting expert: " + ex.getMessage());
        }
    }
}
