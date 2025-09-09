package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoodTrackingPage extends JFrame {

    private JLabel selectedMoodLabel;
    private JSlider intensitySlider;
    private JTextArea noteArea;
    
    public MoodTrackingPage(boolean isRegisteredUser, int loggedInPatientId) {

        setTitle("Mood Tracking");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/assets/mood_tracking_bg.jpg"));
        JLabel bgLabel = new JLabel(bgIcon);
        bgLabel.setBounds(0, 0, 1200, 800);
        bgLabel.setLayout(null);

        // Title
        JLabel title = new JLabel("Track Your Mood", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBounds(400, 30, 400, 40);
        bgLabel.add(title);

        // Mood selection
        JLabel moodLabel = new JLabel("Select Mood:");
        moodLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        moodLabel.setBounds(150, 100, 200, 30);
        bgLabel.add(moodLabel);

        JPanel moodPanel = new JPanel();
        moodPanel.setBounds(150, 140, 900, 60);
        moodPanel.setOpaque(false);
        String[] moods = {"😀", "😐", "😢", "😡", "😌"};
        for (String mood : moods) {
            JButton btn = new JButton(mood);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 28));
            btn.addActionListener(e -> selectedMoodLabel.setText(mood));
            moodPanel.add(btn);
        }
        bgLabel.add(moodPanel);

        // Selected mood display
        selectedMoodLabel = new JLabel("None");
        selectedMoodLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        selectedMoodLabel.setBounds(150, 210, 200, 30);
        bgLabel.add(selectedMoodLabel);

        // Intensity slider
        JLabel intensityLabel = new JLabel("Mood Intensity:");
        intensityLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        intensityLabel.setBounds(150, 260, 200, 30);
        bgLabel.add(intensityLabel);

        intensitySlider = new JSlider(1, 10, 5);
        intensitySlider.setBounds(150, 300, 300, 50);
        intensitySlider.setMajorTickSpacing(1);
        intensitySlider.setPaintTicks(true);
        intensitySlider.setPaintLabels(true);
        bgLabel.add(intensitySlider);

        // Notes area
        JLabel noteLabel = new JLabel("Add a Note:");
        noteLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        noteLabel.setBounds(150, 370, 200, 30);
        bgLabel.add(noteLabel);

        noteArea = new JTextArea();
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setBounds(150, 410, 500, 100);
        bgLabel.add(noteScroll);

        // Save button
        JButton saveBtn = new JButton("Save Mood");
        saveBtn.setBounds(150, 530, 150, 40);
        saveBtn.addActionListener(e -> saveMood());
        bgLabel.add(saveBtn);

        // View Logs button
        JButton viewLogsBtn = new JButton("View Mood Logs");
        viewLogsBtn.setBounds(320, 530, 200, 40);
        viewLogsBtn.addActionListener(e -> new MoodLogsPage(isRegisteredUser));
        bgLabel.add(viewLogsBtn);

        // Back to Homepage
        JButton backBtn = new JButton("Back to Homepage");
        backBtn.setBounds(540, 530, 200, 40);
        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });
        bgLabel.add(backBtn);

        add(bgLabel);
        setVisible(true);
    }

    private void saveMood() {
        if (!SessionManager.isLoggedIn()) {
        JOptionPane.showMessageDialog(this, "You must be logged in.");
        return;
    }


        String mood = selectedMoodLabel.getText();
        int intensity = intensitySlider.getValue();
        String note = noteArea.getText().trim();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if (mood.equals("None")) {
            JOptionPane.showMessageDialog(this, "Please select a mood.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO mood_logs (user_id, mood, intensity, note, log_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, SessionManager.getCurrentUserId());
            stmt.setString(2, mood);
            stmt.setInt(3, intensity);
            stmt.setString(4, note);
            stmt.setString(5, date);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Mood saved successfully!");

            noteArea.setText("");
            selectedMoodLabel.setText("None");
            intensitySlider.setValue(5);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving mood: " + ex.getMessage());
        }
    }

    // Mood logs display page (fetch from DB)
    static class MoodLogsPage extends JFrame {
        public MoodLogsPage(boolean isRegisteredUser) {
            setTitle("Mood Logs");
            setSize(800, 600);
            setLocationRelativeTo(null);

            String[] columns = {"Date", "Mood", "Intensity", "Note"};
            String[][] data;

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT log_date, mood, intensity, note FROM mood_logs WHERE user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, SessionManager.getCurrentUserId());
                ResultSet rs = stmt.executeQuery();

                List<String[]> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(new String[]{
                            rs.getString("log_date"),
                            rs.getString("mood"),
                            String.valueOf(rs.getInt("intensity")),
                            rs.getString("note")
                    });
                }

                data = rows.toArray(new String[0][0]);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching logs: " + ex.getMessage());
                data = new String[0][4];
            }

            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(e -> {
                dispose();
                new MoodTrackingPage(isRegisteredUser, SessionManager.getCurrentUserId());
            });
            add(backBtn, BorderLayout.SOUTH);

            setVisible(true);
        }
    }
}
