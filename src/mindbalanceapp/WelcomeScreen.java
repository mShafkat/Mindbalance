package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class WelcomeScreen extends JFrame {

    public WelcomeScreen() {
        setTitle("MindBalance - Welcome");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Absolute positioning

        // Load background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/welcome_bg.jpg"));
        JLabel backgroundLabel = new JLabel(bgIcon);
        backgroundLabel.setBounds(0, 0, 1200, 800);
        backgroundLabel.setLayout(null);

        // Button size & spacing
        int buttonWidth = 220;
        int buttonHeight = 50;
        int spacing = 30;
        int totalWidth = (2 * buttonWidth) + spacing;
        int xStart = (1200 - totalWidth) / 2;
        int yPosition = 500;

        // Create modern buttons
        JButton getStartedBtn = createModernButton("Get Started Free", new Color(0x4FC3F7), new Color(0x0288D1));
        getStartedBtn.setBounds(xStart, yPosition, buttonWidth, buttonHeight);

        JButton signInBtn = createModernButton("Sign In", new Color(0x90A4AE), new Color(0x455A64));
        signInBtn.setBounds(xStart + buttonWidth + spacing, yPosition, buttonWidth, buttonHeight);

        JButton loginBtn = createModernButton("Log In", new Color(0x212121), new Color(0x000000));
        loginBtn.setBounds((1200 - buttonWidth) / 2, yPosition + buttonHeight + 40, buttonWidth, buttonHeight);

        // Button actions
        getStartedBtn.addActionListener(e -> {
            SessionManager.setUnregisteredUser();
            new HomePage().setVisible(true);
            dispose();
        });

        signInBtn.addActionListener(e -> {
            new SignInRolePage().setVisible(true);
            dispose();
        });

        loginBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });

        // Add to background
        backgroundLabel.add(getStartedBtn);
        backgroundLabel.add(signInBtn);
        backgroundLabel.add(loginBtn);
        add(backgroundLabel);

        setVisible(true);
    }

    // 🔹 Method to create a modern button with hover effect
    private JButton createModernButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, baseColor, getWidth(), getHeight(), baseColor.darker());
                g2.setPaint(gp);

                Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25);
                g2.fill(shape);

                // Draw text
                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // no border
            }
        };

        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 🔹 Hover effect with MouseListener
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setOpaque(true);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(null);
                button.setOpaque(false);
                button.repaint();
            }
        });

        return button;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new WelcomeScreen();
    }
}
