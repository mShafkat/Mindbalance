package mindbalanceapp;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JFrame {

    private boolean isRegistered;

    public AdminPanel(boolean isRegistered) {
        this.isRegistered = isRegistered;

        setTitle("Admin Panel");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // 🔲 Background Image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/admin_panel_bg.jpg")); // You will provide this
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);
        

        // 🔲 Title Label
        JLabel title = new JLabel("Admin Panel", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 35));
        title.setForeground(Color.BLACK);
        title.setBounds(350, 40, 500, 60);
        bgLabel.add(title);

        // 🔲 Buttons for Admin Options (Export Data removed)
        String[] adminOptions = {
            "View All Appointments",
            "View All Registered Users",
            "View Mood Test Results",
            "Manage Expert Profiles",
            "Back to Homepage"
        };

        int y = 180; // Adjusted starting position for better centering
        for (String option : adminOptions) {
            JButton btn = new JButton(option);
            btn.setBounds(390, y, 420, 55);
            btn.setFont(new Font("SansSerif", Font.BOLD, 20));
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true));

            btn.addActionListener(e -> {
                switch (option) {
                    case "View All Appointments":
                        dispose();
                        new AdminAppointmentsPage();
                        break;
                    case "View All Registered Users":
                        dispose();
                        new AdminUsersPage(isRegistered);
                        break;
                    case "View Mood Test Results":
                        dispose();
                        new AdminMoodTestResultsPage(isRegistered);
                        break;
                    case "Manage Expert Profiles":
                        dispose();
                        new AdminManageExpertsPage(isRegistered);
                        break;
                    case "Back to Homepage":
                        dispose();
                        new HomePage();
                        break;
                }
            });

            bgLabel.add(btn);
            y += 80;
        }

        add(bgLabel);
        setVisible(true);
    }
}
