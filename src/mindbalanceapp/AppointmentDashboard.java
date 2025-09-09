package mindbalanceapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AppointmentDashboard extends JFrame {

    private JTable table;
    private int userId;
    private boolean isExpert;

    public AppointmentDashboard(int userId, boolean isExpert) {
        this.userId = userId;
        this.isExpert = isExpert;

        setTitle(isExpert ? "My Appointment Dashboard" : "Available Slots");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        if (isExpert) {
            loadExpertSlots();
        } else {
            loadAvailableSlots();
        }

        setVisible(true);
    }

    // EXPERT VIEW
    private void loadExpertSlots() {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT s.slot_id, s.slot_datetime, s.is_booked, u.name AS patient " +
                     "FROM expert_slots s " +
                     "LEFT JOIN appointments a ON s.slot_id = a.slot_id " +
                     "LEFT JOIN users u ON a.patient_id = u.user_id " +
                     "WHERE s.expert_id = ? " +
                     "ORDER BY s.slot_datetime";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Slot ID", "Date & Time", "Booked", "Patient"}, 0
        );
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("slot_id"),
                rs.getTimestamp("slot_datetime"),
                rs.getBoolean("is_booked") ? "Yes" : "No",
                rs.getString("patient") != null ? rs.getString("patient") : "-"
            });
        }
        table.setModel(model);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading slots: " + e.getMessage());
    }
}


    // PATIENT VIEW
    private void loadAvailableSlots() {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT s.slot_id, s.slot_datetime, e.name AS expert " +
                     "FROM expert_slots s " +
                     "JOIN experts e ON s.expert_id = e.expert_id " +
                     "WHERE s.is_booked = FALSE " +
                     "ORDER BY s.slot_datetime";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Slot ID", "Date & Time", "Expert"}, 0
        );
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("slot_id"),
                rs.getTimestamp("slot_datetime"),
                rs.getString("expert")
            });
        }
        table.setModel(model);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading available slots: " + e.getMessage());
    }
}

}
