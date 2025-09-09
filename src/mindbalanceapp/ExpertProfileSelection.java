package mindbalanceapp;

import javax.swing.*;
import java.awt.*;

public class ExpertProfileSelection extends JFrame {

    public ExpertProfileSelection(boolean isRegistered) {
        

        setTitle("Expert Profile");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/expert_bg.jpg"));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);

        // Psychologist Image
        ImageIcon psychoIcon = new ImageIcon(getClass().getResource("/assets/psychologist.png"));
        Image scaledPsycho = psychoIcon.getImage().getScaledInstance(240, 240, Image.SCALE_SMOOTH);
        JLabel psychoLabel = new JLabel(new ImageIcon(scaledPsycho));
        psychoLabel.setBounds(300, 200, 240, 240);

        // Psychiatrist Image
        ImageIcon psychiaIcon = new ImageIcon(getClass().getResource("/assets/psychiatrist.png"));
        Image scaledPsychia = psychiaIcon.getImage().getScaledInstance(240, 240, Image.SCALE_SMOOTH);
        JLabel psychiaLabel = new JLabel(new ImageIcon(scaledPsychia));
        psychiaLabel.setBounds(660, 200, 240, 240);

        // Psychologist Button
        JButton psychologistBtn = new JButton("Psychologist");
        psychologistBtn.setBounds(300, 460, 240, 45);
        psychologistBtn.setBackground(Color.BLACK);
        psychologistBtn.setForeground(Color.BLACK);
        psychologistBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        psychologistBtn.setFocusPainted(false);
        psychologistBtn.addActionListener(e -> {
        dispose();
        new ExpertListPage("psychologist", isRegistered);
        });

        // Psychiatrist Button
        JButton psychiatristBtn = new JButton("Psychiatrist");
        psychiatristBtn.setBounds(660, 460, 240, 45);
        psychiatristBtn.setBackground(Color.BLACK);
        psychiatristBtn.setForeground(Color.BLACK);
        psychiatristBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        psychiatristBtn.setFocusPainted(false);
        psychiatristBtn.addActionListener(e -> {
        dispose();
        new ExpertListPage("psychiatrist", isRegistered);
        });

        // Back Button
        JButton backBtn = new JButton("← Back");
        backBtn.setBounds(30, 700, 120, 40);
        backBtn.setBackground(Color.BLACK);
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            new HomePage();
            dispose();
        });

        // Add all components
        bgLabel.add(psychoLabel);
        bgLabel.add(psychiaLabel);
        bgLabel.add(psychologistBtn);
        bgLabel.add(psychiatristBtn);
        bgLabel.add(backBtn);

        add(bgLabel);
        setVisible(true);
    }
}