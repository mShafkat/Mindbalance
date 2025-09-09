/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mindbalanceapp;

import javax.swing.*;
import java.awt.*;

public class SignInRolePage extends JFrame {

    public SignInRolePage() {
        setTitle("MindBalance - Choose Role");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/signin_bg.jpg"));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);

        int buttonWidth = 300;
        int buttonHeight = 60;
        int x = (1200 - buttonWidth) / 2;
        int startY = 300;
        int spacing = 80;

        JButton patientBtn = createButton("Sign in as Patient", x, startY);
        JButton psychologistBtn = createButton("Sign in as Psychologist", x, startY + spacing);
        JButton psychiatristBtn = createButton("Sign in as Psychiatrist", x, startY + 2 * spacing);

        JButton backBtn = createButton("← Back", 30, 700);
        backBtn.setSize(120, 40);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Actions
        patientBtn.addActionListener(e -> {
            dispose();
            new PatientRegistrationPage().setVisible(true);
        });

        psychologistBtn.addActionListener(e -> {
            dispose();
            new ProfessionalRegistrationPage("psychologist").setVisible(true);
        });

        psychiatristBtn.addActionListener(e -> {
            dispose();
            new ProfessionalRegistrationPage("psychiatrist").setVisible(true);
        });


        backBtn.addActionListener(e -> {
            new WelcomeScreen();
            dispose();
        });

        // Add components
        bgLabel.add(patientBtn);
        bgLabel.add(psychologistBtn);
        bgLabel.add(psychiatristBtn);
        bgLabel.add(backBtn);

        add(bgLabel);
        setVisible(true);
    }

    private JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 300, 60);
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        return btn;
    }
}

