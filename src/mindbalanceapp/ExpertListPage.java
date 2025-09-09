package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;


public class ExpertListPage extends JFrame {

    private final List<Expert> experts = new ArrayList<>();
    private final String role;
    private final boolean isRegistered;


    public ExpertListPage(String role, boolean isRegistered) {
        this.role = role;
        this.isRegistered = isRegistered;

        setTitle(role + " List");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background label
        JLabel bgLabel = new JLabel(safeIcon(getClass(), "/assets/expert_list_bg.jpg", 1200, 800));
        bgLabel.setLayout(null);
        add(bgLabel);

        // Scrollable expert panel with GridLayout (5 cards per row)
        JPanel expertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        expertPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(expertPanel);
        scrollPane.setBounds(60, 80, 1080, 580);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        bgLabel.add(scrollPane);

        // Load experts from DB
        loadExperts();

        // Populate cards
        for (Expert ex : experts) {
            JPanel card = createExpertCard(ex);
            expertPanel.add(card);
        }

        // Back button
        JButton backBtn = new JButton("← Back");
        backBtn.setBounds(30, 700, 100, 35);
        backBtn.setBackground(new Color(255, 255, 255, 220));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.addActionListener(e -> {
        ExpertProfileSelection eps = new ExpertProfileSelection(isRegistered);
        eps.setVisible(true);  // make sure the new frame is visible
        dispose();             // close current ExpertListPage
        });
        bgLabel.add(backBtn);

        setVisible(true);
    }

    private JPanel createExpertCard(Expert ex) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(220, 320));
        card.setBackground(new Color(0, 0, 0, 180));
        card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel imgLabel = new JLabel(safeIcon(getClass(), "/assets/expert_placeholder.jpg", 140, 140));
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel("<html><center>" + ex.name + "</center></html>");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel designation = new JLabel("<html><center>" + ex.designation + "</center></html>");
        designation.setForeground(Color.WHITE);
        designation.setFont(new Font("SansSerif", Font.PLAIN, 13));
        designation.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel degree = new JLabel("<html><center>" + ex.degree + "</center></html>");
        degree.setForeground(Color.WHITE);
        degree.setFont(new Font("SansSerif", Font.PLAIN, 12));
        degree.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(imgLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(name);
        card.add(designation);
        card.add(degree);
        card.add(Box.createVerticalStrut(15));

        if (isRegistered) {
            JButton bookBtn = new JButton("Book Appointment");
            bookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            bookBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            bookBtn.setBackground(Color.WHITE);
            bookBtn.setForeground(Color.BLACK);
            JFrame parentFrame = ExpertListPage.this; // reference to current JFrame
//            int loggedInPatientId = getLoggedInUserId(); // must return int

            bookBtn.addActionListener(e -> {
            int patientId = SessionManager.getCurrentUserId();
            new AppointmentFormPage(ExpertListPage.this, ex.expertId, ex.name, patientId).setVisible(true);;
            });


            card.add(bookBtn);
        } else {
            JLabel infoLabel = new JLabel("Login to book");
            infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            infoLabel.setForeground(Color.LIGHT_GRAY);
            card.add(infoLabel);
        }

        return card;
    }

    private void loadExperts() {
        experts.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT expert_id, expertsName, role FROM experts WHERE status = 'active' AND LOWER(role) = LOWER(?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, role);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int expertId = rs.getInt("expert_id");
                String name = rs.getString("expertsName");
                String designation = rs.getString("role");
                String degree = "";
                String bmdc = "";
                experts.add(new Expert(expertId, name, designation, degree, bmdc));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load experts.");
        }
    }

    public static ImageIcon safeIcon(Class<?> cls, String path, int w, int h) {
        java.net.URL url = cls.getResource(path);
        if (url == null) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, w, h);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(1, 1, w - 3, h - 3);
            g.dispose();
            return new ImageIcon(img);
        }
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    static class Expert {
        int expertId;
        String name;
        String designation;
        String degree;
        String bmdc;

        public Expert(int expertId, String name, String designation, String degree, String bmdc) {
            this.expertId = expertId;
            this.name = name;
            this.designation = designation;
            this.degree = degree;
            this.bmdc = bmdc;
        }
    }
}
