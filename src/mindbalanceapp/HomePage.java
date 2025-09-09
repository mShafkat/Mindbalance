package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomePage extends JFrame {

    public HomePage() {

        setTitle("MindBalance - Home");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🔲 Background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/home_bg.jpg"));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(new BorderLayout());

        // 🔲 Top Panel for Greeting
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15)); // less padding
        topPanel.setOpaque(false);
        String currentUserName = SessionManager.getCurrentUserName();
        String currentUserRole = SessionManager.getCurrentUserRole();
        JLabel greetingLabel;
        if ("expert".equalsIgnoreCase(currentUserRole)) {
            greetingLabel = new JLabel("👋 Hello Doc " + currentUserName + "!");
        } else {
            greetingLabel = new JLabel("👋 Hello " + currentUserName + "!");
        }
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 28)); // bigger font
        greetingLabel.setForeground(Color.WHITE);
        topPanel.add(greetingLabel);
        bgLabel.add(topPanel, BorderLayout.NORTH);

        // 🔲 Transparent main panel for buttons/icons
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        bgLabel.add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // tighter spacing between cards

        // 🔲 Labels & Icons
        String[] labels = {"Mood Test", "Expert Profile", "Mood Tracking", "Appointment", "Admin Panel"};
        String[] imagePaths = {
                "/assets/test.png",
                "/assets/expert.png",
                "/assets/mood.png",
                "/assets/appointment.png",
                "/assets/admin.png"
        };

        JButton[] buttons = new JButton[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setOpaque(false);

            // Icon
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePaths[i]));
            Image resizedImage = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH); // bigger icons
            JLabel imgLabel = new JLabel(new ImageIcon(resizedImage));
            imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Button
            JButton button = new JButton(labels[i]);
            buttons[i] = button;
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(new Dimension(220, 60)); // bigger buttons
            button.setBackground(new Color(35, 35, 35));
            button.setForeground(Color.black);
            button.setFocusPainted(false);
            button.setFont(new Font("SansSerif", Font.BOLD, 18));
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            // Hover effect
            final JButton hoverButton = button;
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hoverButton.setBackground(new Color(0x4FC3F7));
                    hoverButton.setForeground(Color.BLACK);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverButton.setBackground(new Color(35, 35, 35));
                    hoverButton.setForeground(Color.black);
                }
            });

            // Access control for guests/admin
            boolean isGuest = "unregistered".equalsIgnoreCase(SessionManager.getCurrentUserRole());
            if (isGuest && (labels[i].equals("Mood Tracking") || labels[i].equals("Appointment") || labels[i].equals("Admin Panel"))) {
                button.setEnabled(false);
            }

            if (labels[i].equals("Admin Panel")) {
                if (!SessionManager.isAdmin()) {
                    button.addActionListener(e ->
                            JOptionPane.showMessageDialog(this, "Only system admins can access the Admin Panel.")
                    );
                } else {
                    button.addActionListener(e -> {
                        dispose();
                        new AdminPanel(true);
                    });
                }
            }

            card.add(imgLabel);
            card.add(Box.createVerticalStrut(15)); // bigger space between icon and button
            card.add(button);

            gbc.gridx = i % 3;
            gbc.gridy = i / 3;
            mainPanel.add(card, gbc);
        }

        // 🔲 Back button under cards, small
        JButton backBtn = new JButton("← Back");
        backBtn.setPreferredSize(new Dimension(120, 40));
        backBtn.setBackground(new Color(20, 20, 20));
        backBtn.setForeground(Color.black);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            new WelcomeScreen();
            dispose();
        });

        GridBagConstraints backGbc = new GridBagConstraints();
        backGbc.gridx = 0;
        backGbc.gridy = 2; // row under cards
        backGbc.gridwidth = 3; // span all columns
        backGbc.insets = new Insets(30, 0, 20, 0); // space above and below
        backGbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(backBtn, backGbc);

        add(bgLabel);
        setVisible(true);

        // 🔲 Button Actions
        buttons[0].addActionListener(e -> { dispose(); new DisorderSelectionPage(); });
        buttons[1].addActionListener(e -> {
            boolean isRegistered = SessionManager.isLoggedIn();
            new ExpertProfileSelection(isRegistered);
            dispose();
        });
        buttons[2].addActionListener(e -> {
            dispose();
            new MoodTrackingPage(SessionManager.isLoggedIn(), SessionManager.getCurrentUserId());
        });
        buttons[3].addActionListener(e -> {
            if ("expert".equalsIgnoreCase(currentUserRole)) {
                new ExpertSlotManager(SessionManager.getCurrentUserId());
            } else if ("patient".equalsIgnoreCase(currentUserRole)) {
                new ExpertProfileSelection(true);
            } else {
                JOptionPane.showMessageDialog(this, "You need to be logged in to access appointments.");
            }
        });
    }
}
