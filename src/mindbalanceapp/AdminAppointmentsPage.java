package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class AdminAppointmentsPage extends JFrame {

    public AdminAppointmentsPage() {
        setTitle("All Appointments");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/admin_appointments_bg.jpg"));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);

        // Title
        JLabel title = new JLabel("All Appointments", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.BLACK);
        title.setBounds(400, 30, 400, 50);
        bgLabel.add(title);

        // List panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBounds(150, 100, 900, 550);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        bgLabel.add(scrollPane);

        // Load DB data
        List<Appointment> appointments = loadAppointments();

        Font labelFont = new Font("SansSerif", Font.PLAIN, 18);
        Font boldFont = new Font("SansSerif", Font.BOLD, 18);

        for (Appointment appt : appointments) {
            JPanel card = new JPanel();
            card.setLayout(new GridLayout(0, 1));
            card.setPreferredSize(new Dimension(880, 120));
            card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            card.setBackground(new Color(255, 255, 255, 230));

            card.add(createStyledLabel("👤 Patient: ", appt.patientName + " (Age: " + appt.age + ")", boldFont, labelFont));
            card.add(createStyledLabel("📞 Phone: ", appt.phone, boldFont, labelFont));
            card.add(createStyledLabel("📝 Problem: ", appt.problem, boldFont, labelFont));
            card.add(createStyledLabel("👨‍⚕ Expert: ", appt.expertName, boldFont, labelFont));
            card.add(createStyledLabel("📅 Date: ", appt.date, boldFont, labelFont));
            card.add(createStyledLabel("📌 Status: ", appt.status, boldFont, labelFont));

            listPanel.add(Box.createVerticalStrut(12));
            listPanel.add(card);
        }

        // Back Button
        JButton backBtn = new JButton("← Back");
        backBtn.setBounds(30, 700, 120, 40);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        backBtn.setBackground(new Color(30, 144, 255));
        backBtn.setForeground(Color.black);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        backBtn.addActionListener(e -> {
            dispose();
            new AdminPanel(true);
        });
        bgLabel.add(backBtn);

        add(bgLabel);
        setVisible(true);
    }

    private static class Appointment {
        String patientName, phone, problem, expertName, date, status;
        int age;

        public Appointment(String patientName, int age, String phone, String problem, String expertName, String date, String status) {
            this.patientName = patientName;
            this.age = age;
            this.phone = phone;
            this.problem = problem;
            this.expertName = expertName;
            this.date = date;
            this.status = status;
        }
    }

    // Helper to calculate age from DOB
    private int calculateAge(Date dob) {
        if (dob == null) return 0;
        LocalDate birthDate = dob.toLocalDate();
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private List<Appointment> loadAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = """
            SELECT a.appointment_id, a.appointment_date, a.status,
                   u.name AS patient_name, u.patientDOB, u.phone, a.problem,
                   e.expertsName AS expert_name
            FROM appointments a
            JOIN users u ON a.patient_id = u.user_id
            JOIN experts e ON a.expert_id = e.expert_id
            ORDER BY a.appointment_date DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Date dob = rs.getDate("patientDOB");
                int age = calculateAge(dob);

                list.add(new Appointment(
                        rs.getString("patient_name"),
                        age,
                        rs.getString("phone"),
                        rs.getString("problem"),
                        rs.getString("expert_name"),
                        rs.getString("appointment_date"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Failed to load appointments: " + ex.getMessage());
        }

        return list;
    }

    // Helper method to create bold label + regular value inline
    private JPanel createStyledLabel(String label, String value, Font labelFont, Font valueFont) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(labelFont);
        JLabel val = new JLabel(value);
        val.setFont(valueFont);
        panel.add(lbl);
        panel.add(val);
        return panel;
    }
}