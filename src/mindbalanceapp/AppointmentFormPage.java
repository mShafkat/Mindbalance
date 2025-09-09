package mindbalanceapp;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;


public class AppointmentFormPage extends JDialog {

    private final int expertId;
    private final String expertName;
    private final int patientId;

    private JTextArea problemArea;
    private JDateChooser dateChooser;
    private JComboBox<String> timeSlotCombo;
    private List<Integer> slotIds; // Store slot_id for selected times

    public AppointmentFormPage(JFrame parent, int expertId, String expertName, int patientId) {
        super(parent, "Book Appointment", true);
        this.expertId = expertId;
        this.expertName = expertName;
        this.patientId = patientId;
        this.slotIds = new ArrayList<>();

        initUI(parent);
    }

    private void initUI(JFrame parent) {
        setSize(650, 550);
        setLocationRelativeTo(parent);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel bgPanel = new JPanel(null);
        bgPanel.setBounds(0, 0, 650, 550);
        bgPanel.setBackground(new Color(50, 50, 50, 220));
        add(bgPanel);

        JLabel title = new JLabel("Book Appointment with " + expertName);
        title.setBounds(30, 20, 590, 40);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        bgPanel.add(title);

        // Problem
        JLabel problemLabel = new JLabel("Describe your problem:");
        problemLabel.setBounds(30, 80, 250, 25);
        problemLabel.setForeground(Color.WHITE);
        problemLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bgPanel.add(problemLabel);

        problemArea = new JTextArea();
        problemArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(problemArea);
        scrollPane.setBounds(30, 110, 580, 120);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        bgPanel.add(scrollPane);

        // Date
        JLabel dateLabel = new JLabel("Select Appointment Date:");
        dateLabel.setBounds(30, 250, 200, 25);
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bgPanel.add(dateLabel);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setBounds(30, 280, 200, 35);
        bgPanel.add(dateChooser);

        // Time
        JLabel timeLabel = new JLabel("Select Time Slot:");
        timeLabel.setBounds(260, 250, 200, 25);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bgPanel.add(timeLabel);

        timeSlotCombo = new JComboBox<>();
        timeSlotCombo.setBounds(260, 280, 150, 35);
        timeSlotCombo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        bgPanel.add(timeSlotCombo);

        // When date changes, load available slots
        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                loadTimeSlots();
            }
        });

        // Book Button
        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.setBounds(30, 340, 200, 40);
        bookBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        bookBtn.setBackground(new Color(0x4FC3F7));
        bookBtn.setForeground(Color.BLACK);
        bookBtn.setFocusPainted(false);
        bookBtn.addActionListener(e -> {
            if (bookAppointment()) dispose();
        });
        bgPanel.add(bookBtn);

        // Cancel Button
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(250, 340, 150, 40);
        cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        cancelBtn.setBackground(new Color(0xFF8A65));
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());
        bgPanel.add(cancelBtn);
    }

    private void loadTimeSlots() {
        timeSlotCombo.removeAllItems();
        slotIds.clear();

        if (dateChooser.getDate() == null) return;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT slot_id, slot_datetime FROM expert_availability " +
                         "WHERE expert_id = ? AND DATE(slot_datetime) = ? AND is_available = 1 " +
                         "ORDER BY slot_datetime";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, expertId);
            pst.setDate(2, new java.sql.Date(dateChooser.getDate().getTime()));
            ResultSet rs = pst.executeQuery();

            boolean hasSlots = false;
            while (rs.next()) {
                hasSlots = true;
                slotIds.add(rs.getInt("slot_id"));
                // Format time as hh:mm AM/PM
                String formattedTime = rs.getTimestamp("slot_datetime")
                    .toLocalDateTime()
                    .toLocalTime()
                    .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
                timeSlotCombo.addItem(formattedTime);
            }

            if (!hasSlots) {
                timeSlotCombo.addItem("No available slots");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading time slots: " + ex.getMessage());
        }
    }

    private boolean isPatientFree(int patientId, Timestamp slotDateTime) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM appointments WHERE patient_id = ? AND appointment_date = ? AND status != 'cancelled'";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, patientId);
            pst.setTimestamp(2, slotDateTime);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false; // assume not free on error
    }

    private boolean bookAppointment() {
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select a date.");
            return false;
        }

        String problem = problemArea.getText().trim();
        if (problem.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please describe your problem.");
            return false;
        }

        int selectedIndex = timeSlotCombo.getSelectedIndex();
        if (selectedIndex < 0 || slotIds.isEmpty() || "No available slots".equals(timeSlotCombo.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Please select a valid time slot.");
            return false;
        }

        int slotId = slotIds.get(selectedIndex);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Fetch the exact slot datetime from expert_availability
            String slotSql = "SELECT slot_datetime FROM expert_availability WHERE slot_id = ?";
            PreparedStatement pstSlot = conn.prepareStatement(slotSql);
            pstSlot.setInt(1, slotId);
            ResultSet rs = pstSlot.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Selected slot not found.");
                return false;
            }
            Timestamp slotDateTime = rs.getTimestamp("slot_datetime");

            // Check if patient already has an appointment at this time
            if (!isPatientFree(patientId, slotDateTime)) {
                JOptionPane.showMessageDialog(this, "You already have an appointment at this time!");
                return false;
            }

            // Fetch patient's DOB and calculate age
String dobSql = "SELECT patientDOB FROM users WHERE user_id = ?";
PreparedStatement pstDob = conn.prepareStatement(dobSql);
pstDob.setInt(1, patientId);
ResultSet rsDob = pstDob.executeQuery(); 
int age = 0;
if (rsDob.next()) {
    java.sql.Date dob = rsDob.getDate("patientDOB");
    if (dob != null) {
        LocalDate birthDate = dob.toLocalDate();
        age = Period.between(birthDate, LocalDate.now()).getYears();
    }
}

// Insert appointment
String insertSql = "INSERT INTO appointments (patient_id, expert_id, slot_id, appointment_date, status, problem, age) " +
                   "VALUES (?, ?, ?, ?, 'pending', ?, ?)";
PreparedStatement pstInsert = conn.prepareStatement(insertSql);
pstInsert.setInt(1, patientId);
pstInsert.setInt(2, expertId);
pstInsert.setInt(3, slotId); // actual slot_id
pstInsert.setTimestamp(4, slotDateTime);
pstInsert.setString(5, problem);
pstInsert.setInt(6, age);
pstInsert.executeUpdate();



            // Mark slot as unavailable
            String updateSlot = "UPDATE expert_availability SET is_available = 0 WHERE slot_id = ?";
            PreparedStatement pst2 = conn.prepareStatement(updateSlot);
            pst2.setInt(1, slotId);
            pst2.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
            loadTimeSlots(); // refresh available slots
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage());
            return false;
        }
    }
}
