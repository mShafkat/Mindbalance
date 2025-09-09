package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class AdminExportDataPage extends JFrame {

    public AdminExportDataPage(boolean isRegisteredUser) {
        setTitle("Export Data");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Set background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/expert_bg.jpg"));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);

        JLabel title = new JLabel("Export Data to CSV", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.BLACK);
        title.setBounds(400, 50, 400, 40);
        bgLabel.add(title);

        JButton exportButton = new JButton("Export Dummy Data");
        exportButton.setBounds(460, 150, 280, 50);
        exportButton.setBackground(Color.BLACK);
        exportButton.setForeground(Color.BLACK);
        exportButton.setFont(new Font("SansSerif", Font.BOLD, 18));

        exportButton.addActionListener(e -> {
            try (FileWriter writer = new FileWriter("exported_data.csv")) {
                writer.write("Name,Age,Phone,Disorder,Score\n");
                writer.write("Alice,22,017XXXXXXXX,Anxiety,15\n");
                writer.write("Bob,30,018XXXXXXXX,OCD,28\n");
                writer.write("Charlie,27,019XXXXXXXX,MDD,33\n");
                JOptionPane.showMessageDialog(this, "Data exported to exported_data.csv");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        });
        bgLabel.add(exportButton);

        JButton backBtn = new JButton("Back to Admin");
        backBtn.setBounds(50, 700, 160, 35);
        backBtn.setBackground(Color.BLACK);
        backBtn.setForeground(Color.BLACK);
        backBtn.addActionListener(e -> {
            dispose();
            new AdminPanel(isRegisteredUser);
        });
        bgLabel.add(backBtn);

        JButton homeBtn = new JButton("Homepage");
        homeBtn.setBounds(1000, 700, 140, 35);
        homeBtn.setBackground(Color.BLACK);
        homeBtn.setForeground(Color.BLACK);
        homeBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });
        bgLabel.add(homeBtn);

        add(bgLabel);
        setVisible(true);
    }
}
