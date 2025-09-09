package mindbalanceapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Vector;

public class AdminUsersPage extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JTextField searchField;

    private final boolean isRegisteredUser;

    public AdminUsersPage(boolean isRegisteredUser) {
        this.isRegisteredUser = isRegisteredUser;

        setTitle("Registered Users & Experts");
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        ImageIcon bgIcon = null;
        try {
            bgIcon = new ImageIcon(getClass().getResource("/assets/admin_users_bg.jpg"));
        } catch (Exception ignored) {}
        if (bgIcon == null || bgIcon.getIconWidth() <= 0)
            bgIcon = new ImageIcon("assets/admin_users_bg.jpg");

        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);

        // Title
        JLabel title = new JLabel("All Registered Users & Experts", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.black);
        title.setBounds(300, 20, 600, 50);
        bgLabel.add(title);

        Font uiFont = new Font("SansSerif", Font.PLAIN, 18);

        // Filter dropdown
        String[] filters = {"All", "Patient", "Psychologist", "Psychiatrist", "Admin", "Active", "Pending", "Blocked"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(uiFont);
        filterCombo.setBounds(100, 90, 200, 35);
        bgLabel.add(filterCombo);

        // Search field
        searchField = new JTextField();
        searchField.setFont(uiFont);
        searchField.setBounds(320, 90, 250, 35);
        bgLabel.add(searchField);

        JButton searchBtn = createStyledButton("🔍 Search", uiFont);
        searchBtn.setBounds(590, 90, 120, 35);
        bgLabel.add(searchBtn);

        JButton clearBtn = createStyledButton("❌ Clear", uiFont);
        clearBtn.setBounds(720, 90, 120, 35);
        bgLabel.add(clearBtn);

        // Table setup
        String[] columnNames = {"ID", "Name", "Email", "Type", "Role", "Phone", "Status", "Created", "Age"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(28);
        userTable.setFont(uiFont);

        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 18));

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBounds(100, 150, 1000, 470);
        bgLabel.add(scrollPane);

        // Back Button
        JButton backBtn = createStyledButton("← Back", uiFont);
        backBtn.setBounds(30, 680, 140, 40);
        backBtn.addActionListener(e -> {
            dispose();
            new AdminPanel(isRegisteredUser).setVisible(true);
        });
        bgLabel.add(backBtn);

        // Refresh Button
        JButton refreshBtn = createStyledButton("🔄 Refresh", uiFont);
        refreshBtn.setBounds(1020, 680, 140, 40);
        refreshBtn.addActionListener(e -> loadUsers(null, null));
        bgLabel.add(refreshBtn);

        add(bgLabel);

        // Button actions
        searchBtn.addActionListener(e ->
                loadUsers((String) filterCombo.getSelectedItem(), searchField.getText().trim()));

        clearBtn.addActionListener(e -> {
            filterCombo.setSelectedIndex(0);
            searchField.setText("");
            loadUsers(null, null);
        });

        // Initial load
        loadUsers(null, null);

        setVisible(true);
    }

    private JButton createStyledButton(String text, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(30, 144, 255));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return btn;
    }

    private void loadUsers(String filter, String keyword) {
        tableModel.setRowCount(0);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT user_id AS id, name, email, 'User' AS type, role, phone, status, created_at, patientDOB ")
           .append("FROM users WHERE 1=1 ");
        if (filter != null && !"All".equalsIgnoreCase(filter)) {
            if (filter.equalsIgnoreCase("Active") || filter.equalsIgnoreCase("Pending") || filter.equalsIgnoreCase("Blocked")) {
                sql.append("AND status = '").append(filter.toLowerCase()).append("' ");
            } else {
                sql.append("AND role = '").append(filter.toLowerCase()).append("' ");
            }
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (name LIKE ? OR email LIKE ?) ");
        }

        sql.append("UNION ALL ");
        sql.append("SELECT expert_id AS id, expertsName AS name, expertEmail AS email, 'Expert' AS type, role, phone, status, NULL AS created_at, NULL AS patientDOB ")
           .append("FROM experts WHERE 1=1 ");
        if (filter != null && !"All".equalsIgnoreCase(filter)) {
            if (filter.equalsIgnoreCase("Active") || filter.equalsIgnoreCase("Pending") || filter.equalsIgnoreCase("Blocked")) {
                sql.append("AND status = '").append(filter.toLowerCase()).append("' ");
            } else if (filter.equalsIgnoreCase("Psychologist") || filter.equalsIgnoreCase("Psychiatrist")) {
                sql.append("AND role = '").append(filter.toLowerCase()).append("' ");
            }
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (expertsName LIKE ? OR expertEmail LIKE ?) ");
        }

        sql.append("ORDER BY name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {

            if (keyword != null && !keyword.isEmpty()) {
                pst.setString(1, "%" + keyword + "%");
                pst.setString(2, "%" + keyword + "%");
                pst.setString(3, "%" + keyword + "%");
                pst.setString(4, "%" + keyword + "%");
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("type"));
                row.add(rs.getString("role"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("status"));
                row.add(rs.getTimestamp("created_at"));

                // Calculate age if patientDOB exists
                Date dob = rs.getDate("patientDOB");
                if (dob != null) {
                    LocalDate birthDate = dob.toLocalDate();
                    int age = Period.between(birthDate, LocalDate.now()).getYears();
                    row.add("Age: " + age);
                } else {
                    row.add("-");
                }

                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error loading users/experts: " + ex.getMessage());
        }
    }
}