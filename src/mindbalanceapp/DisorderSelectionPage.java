package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class DisorderSelectionPage extends JFrame {

    public DisorderSelectionPage() {
        setTitle("Select a Disorder");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        URL bgURL = getClass().getResource("/assets/disorder_selection_bg.jpg");
        if (bgURL == null) {
            JOptionPane.showMessageDialog(this, "Background image not found!");
            return;
        }
        ImageIcon bgIcon = new ImageIcon(bgURL);
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);

        // Disorders & images
        String[] disorders = {
            "OCD", "MDD", "Adjustment Disorder", "Insomnia",
            "Schizophrenia", "Anxiety", "Panic Disorder", "ADHD"
        };
        String[] imageNames = {
            "ocd.png", "mdd.png", "adjustment.png", "insomnia.png",
            "schizophrenia.png", "anxiety.png", "panic.png", "adhd.png"
        };

        int x = 70, y = 100, gapX = 260, gapY = 260;
        for (int i = 0; i < disorders.length; i++) {
            int col = i % 4;
            int row = i / 4;
            int posX = x + col * gapX;
            int posY = y + row * gapY;

            URL iconUrl = getClass().getResource("/assets/" + imageNames[i]);
            if (iconUrl == null) {
                JOptionPane.showMessageDialog(this, "Missing image: " + imageNames[i]);
                continue;
            }

            ImageIcon disorderIcon = new ImageIcon(iconUrl);
            Image scaled = disorderIcon.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaled));
            iconLabel.setBounds(posX, posY, 130, 130);

            JButton disorderBtn = new JButton(disorders[i]);
            disorderBtn.setBounds(posX, posY + 140, 130, 40);
            disorderBtn.setBackground(Color.WHITE);
            disorderBtn.setForeground(Color.BLACK);
            disorderBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
            disorderBtn.setFocusPainted(false);
            disorderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final int index = i;
            disorderBtn.addActionListener(e -> {
                List<MoodTestPage.Question> questions = MoodTestPage.QuestionFactory.getQuestions(disorders[index]);
                dispose();
                // Guests pass -1 as userId, logged-in users will use SessionManager
                int userId = SessionManager.isLoggedIn() ? SessionManager.getCurrentUserId() : -1;
                new MoodTestPage(disorders[index], questions, userId);
            });

            bgLabel.add(iconLabel);
            bgLabel.add(disorderBtn);
        }

        // Back button
        JButton backBtn = new JButton("← Back");
        backBtn.setBounds(30, 700, 120, 40);
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });
        bgLabel.add(backBtn);

        add(bgLabel);
        setVisible(true);
    }
}
