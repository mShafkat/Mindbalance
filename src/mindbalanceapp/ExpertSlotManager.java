package mindbalanceapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExpertSlotManager extends JFrame {

    private final int expertId;
    private JTable slotTable;
    private JTable appointmentTable;

    public ExpertSlotManager(int expertId) {
        this.expertId = expertId;
        if (expertId <= 0) {
            JOptionPane.showMessageDialog(null, "Invalid expert session. Please log in again.");
            return;
        }

        setTitle("Expert Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Fonts for labels, buttons, table headers
        Font tableFont = new Font("SansSerif", Font.PLAIN, 16);
        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);
        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        
        // === Slot Management Tab ===
        JPanel slotPanel = new JPanel(new BorderLayout());
        JLabel slotLabel = new JLabel("Manage Slots");
        slotLabel.setFont(labelFont);
        slotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        slotPanel.add(slotLabel, BorderLayout.NORTH);

        slotTable = new JTable();
        slotTable.setFont(tableFont);
        slotTable.setRowHeight(28);
        slotTable.getTableHeader().setFont(headerFont);
        JScrollPane slotScroll = new JScrollPane(slotTable);
        slotPanel.add(slotScroll, BorderLayout.CENTER);

        JPanel slotButtons = new JPanel();
        JButton addSlotBtn = new JButton("Add Slot");
        JButton deleteSlotBtn = new JButton("Delete Slot");
        JButton manageStatusBtn = new JButton("Manage Status");
        manageStatusBtn.setFont(buttonFont);
        slotButtons.add(manageStatusBtn);
        addSlotBtn.setFont(buttonFont);
        deleteSlotBtn.setFont(buttonFont);
        slotButtons.add(addSlotBtn);
        slotButtons.add(deleteSlotBtn);
        slotPanel.add(slotButtons, BorderLayout.SOUTH);

        addSlotBtn.addActionListener(e -> addSlot());
        deleteSlotBtn.addActionListener(e -> deleteSlot());
        manageStatusBtn.addActionListener(e -> manageSlotStatus());

        tabbedPane.addTab("Manage Slots", slotPanel);

        // === Appointment Management Tab ===
        JPanel appointmentPanel = new JPanel(new BorderLayout());

        JLabel appointmentLabel = new JLabel("Appointments");
        appointmentLabel.setFont(labelFont);
        appointmentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        appointmentPanel.add(appointmentLabel, BorderLayout.NORTH);

        appointmentTable = new JTable();
        appointmentTable.setFont(tableFont);
        appointmentTable.setRowHeight(28);
        appointmentTable.getTableHeader().setFont(headerFont);
        JPanel appointmentButtons = new JPanel();
JButton manageAppStatusBtn = new JButton("Manage Status");
JButton deleteAppointmentBtn = new JButton("Delete Appointment");
manageAppStatusBtn.setFont(buttonFont);
deleteAppointmentBtn.setFont(buttonFont);
appointmentButtons.add(manageAppStatusBtn);
appointmentButtons.add(deleteAppointmentBtn);
appointmentPanel.add(appointmentButtons, BorderLayout.SOUTH);

manageAppStatusBtn.addActionListener(e -> changeAppointmentStatus());
deleteAppointmentBtn.addActionListener(e -> deleteAppointment());

        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
        appointmentPanel.add(appointmentScroll, BorderLayout.CENTER);

        tabbedPane.addTab("Appointments", appointmentPanel);
        add(tabbedPane);

        // Double-click to change appointment status
        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) changeAppointmentStatus();
            }
        });

        // Load initial data
        loadSlots();
        loadAppointments();

        setVisible(true);
    }

    private void loadSlots() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "SELECT slot_id, slot_datetime, is_available FROM expert_availability WHERE expert_id = ? ORDER BY slot_datetime")) {
            pst.setInt(1, expertId);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel(new String[]{"Slot ID", "Date & Time", "Status"}, 0);
            while (rs.next()) {
                String status = rs.getBoolean("is_available") ? "Available" : "Unavailable";
                model.addRow(new Object[]{
                        rs.getInt("slot_id"),
                        rs.getTimestamp("slot_datetime").toLocalDateTime().toString(),
                        status
                });
            }
            slotTable.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading slots: " + ex.getMessage());
        }
    }

    private void loadAppointments() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "SELECT a.appointment_id, u.name, ea.slot_datetime, a.status " +
                             "FROM appointments a " +
                             "JOIN users u ON a.patient_id = u.user_id " +
                             "JOIN expert_availability ea ON a.slot_id = ea.slot_id " +
                             "WHERE a.expert_id = ? ORDER BY ea.slot_datetime")) {
            pst.setInt(1, expertId);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Patient", "Date & Time", "Status"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("appointment_id"),
                        rs.getString("name"),
                        rs.getTimestamp("slot_datetime").toLocalDateTime().toString(),
                        capitalize(rs.getString("status"))
                });
            }
            appointmentTable.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage());
        }
    }

    private void addSlot() {
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        int option = JOptionPane.showOptionDialog(
                this, dateSpinner, "Select Date for Slots",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null
        );
        if (option != JOptionPane.OK_OPTION) return;

        LocalDate selectedDate = ((java.util.Date) dateSpinner.getValue()).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(21, 0);
        List<LocalTime> availableSlots = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (LocalTime time = startTime; !time.isAfter(endTime); time = time.plusMinutes(30)) {
            LocalDateTime slotDateTime = LocalDateTime.of(selectedDate, time);
            if (slotDateTime.isAfter(now)) availableSlots.add(time);
        }

        if (availableSlots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid slots available for the selected date.");
            return;
        }

        String[] slotStrings = availableSlots.stream()
                .map(t -> t.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")))
                .toArray(String[]::new);

        String selectedTimeStr = (String) JOptionPane.showInputDialog(
                this, "Select Slot Time:", "Choose Slot",
                JOptionPane.PLAIN_MESSAGE, null, slotStrings, slotStrings[0]
        );
        if (selectedTimeStr == null) return;

        LocalTime selectedTime = LocalTime.parse(selectedTimeStr, java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
        LocalDateTime finalSlotDateTime = LocalDateTime.of(selectedDate, selectedTime);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "INSERT INTO expert_availability (expert_id, slot_datetime, is_available) VALUES (?, ?, ?)")) {
            pst.setInt(1, expertId);
            pst.setTimestamp(2, Timestamp.valueOf(finalSlotDateTime));
            pst.setBoolean(3, true);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Slot added successfully!");
            loadSlots();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding slot: " + ex.getMessage());
        }
    }

    private void deleteSlot() {
        int row = slotTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a slot to delete.");
            return;
        }

        int slotId = (int) slotTable.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPst = conn.prepareStatement(
                     "SELECT COUNT(*) FROM appointments WHERE slot_id = ?")) {
            checkPst.setInt(1, slotId);
            ResultSet rs = checkPst.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Cannot delete a booked slot!");
                return;
            }

            try (PreparedStatement deletePst = conn.prepareStatement(
                    "DELETE FROM expert_availability WHERE slot_id = ?")) {
                deletePst.setInt(1, slotId);
                deletePst.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Slot deleted successfully!");
            loadSlots();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting slot: " + ex.getMessage());
        }
    }
    private void manageSlotStatus() {
    int row = slotTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a slot to manage status.");
        return;
    }

    int slotId = (int) slotTable.getValueAt(row, 0);
    String currentStatus = (String) slotTable.getValueAt(row, 2);

    String[] options = {"Available", "Unavailable"};
    JComboBox<String> combo = new JComboBox<>(options);
    combo.setSelectedItem(currentStatus);
    combo.setFont(new Font("SansSerif", Font.BOLD, 16));

    int result = JOptionPane.showConfirmDialog(this, combo, "Change Slot Status", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (result != JOptionPane.OK_OPTION) return;

    boolean newStatus = combo.getSelectedItem().equals("Available");

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement("UPDATE expert_availability SET is_available = ? WHERE slot_id = ?")) {
        pst.setBoolean(1, newStatus);
        pst.setInt(2, slotId);
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "Slot status updated!");
        loadSlots();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage());
    }
}



    private void changeAppointmentStatus() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first!");
            return;
        }

        int appointmentId = (int) appointmentTable.getValueAt(row, 0);
        String currentStatus = ((String) appointmentTable.getValueAt(row, 3)).toLowerCase();

        String[] options = {"pending", "completed", "rejected"};
        JComboBox<String> combo = new JComboBox<>(options);
        combo.setSelectedItem(currentStatus);
        combo.setFont(new Font("SansSerif", Font.BOLD, 18));
        combo.setPreferredSize(new Dimension(200, 40));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("Select new status:"), BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Status", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String newStatus = (String) combo.getSelectedItem();
        if (newStatus == null || newStatus.equals(currentStatus)) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "UPDATE appointments SET status = ? WHERE appointment_id = ?")) {
            pst.setString(1, newStatus);
            pst.setInt(2, appointmentId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Status updated successfully!");
            loadAppointments();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage());
        }
    }
    
    private void deleteAppointment() {
    int row = appointmentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select an appointment to delete.");
        return;
    }

    int appointmentId = (int) appointmentTable.getValueAt(row, 0);
    String status = ((String) appointmentTable.getValueAt(row, 3)).toLowerCase();

    if (!status.equals("completed")) {
        JOptionPane.showMessageDialog(this, "Only completed appointments can be deleted!");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this appointment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) return;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement("DELETE FROM appointments WHERE appointment_id = ?")) {
        pst.setInt(1, appointmentId);
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "Appointment deleted!");
        loadAppointments();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error deleting appointment: " + ex.getMessage());
    }
}


    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }
}