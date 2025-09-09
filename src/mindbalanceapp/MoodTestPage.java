package mindbalanceapp;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class MoodTestPage extends JFrame {
    private int currentPage = 0;
    private final List<Question> questions;
    private final String disorder;
    private final boolean isRegisteredUser;
    private final int userId;
    private final int[] answers;
    private final JPanel questionPanel;
    private final JButton nextBtn, backBtn, resultBtn;

    public MoodTestPage(String disorder, List<Question> questions, int userId) {
        this.disorder = disorder;
        this.questions = questions;
        this.userId = userId;
        this.isRegisteredUser = (userId != -1); // 👈 guest if -1
        this.answers = new int[questions.size()];
        Arrays.fill(this.answers, -1);

        setTitle(disorder + " Test");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel bgLabel = new JLabel(new ImageIcon(getClass().getResource("/assets/mood_test_bg.jpg")));
        bgLabel.setLayout(new BorderLayout());
        setContentPane(bgLabel);

        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        bgLabel.add(scrollPane, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setOpaque(false);

        backBtn = new JButton("← Back");
        nextBtn = new JButton("Next →");
        resultBtn = new JButton("See Result");

        backBtn.addActionListener(e -> {
            if (currentPage == 0) {
                dispose();
                new DisorderSelectionPage();
            } else {
                changePage(currentPage - 1);
            }
        });
        nextBtn.addActionListener(e -> changePage(currentPage + 1));
        resultBtn.addActionListener(e -> showResult());

        navPanel.add(backBtn);
        navPanel.add(nextBtn);
        navPanel.add(resultBtn);
        bgLabel.add(navPanel, BorderLayout.SOUTH);

        loadPage();
        setVisible(true);
    }

    private void loadPage() {
        questionPanel.removeAll();

        int start = currentPage * 5;
        int end = Math.min(start + 5, questions.size());

        for (int i = start; i < end; i++) {
        Question q = questions.get(i);
        JPanel qPanel = new JPanel();
        qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
        qPanel.setOpaque(false);

        JTextArea questionText = new JTextArea((i + 1) + ". " + q.text);
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        questionText.setOpaque(false);
        questionText.setEditable(false);
        questionText.setFocusable(false);
        questionText.setForeground(Color.BLACK);
        qPanel.add(questionText);

        ButtonGroup group = new ButtonGroup();
        for (int j = 0; j < q.options.length; j++) {
            int questionIndex = i;
            int optionValue = j;
            JRadioButton rb = new JRadioButton(q.options[j]);
            rb.setOpaque(false);
            rb.setForeground(Color.BLACK);
            if (answers[questionIndex] == optionValue) rb.setSelected(true);
            rb.addActionListener(e -> answers[questionIndex] = optionValue);
            group.add(rb);
            qPanel.add(rb);
        }
            questionPanel.add(qPanel);
        }

        backBtn.setEnabled(true);
        // Keep your original page visibility (you used 3 pages assumption)
        int totalPages = (int) Math.ceil(questions.size() / 5.0);

        backBtn.setEnabled(currentPage > 0);
        nextBtn.setVisible(currentPage < totalPages - 1);
        resultBtn.setVisible(currentPage == totalPages - 1);

        questionPanel.revalidate();
        questionPanel.repaint();
    }

    private void changePage(int newPage) {
        int totalPages = (int) Math.ceil(questions.size() / 5.0);
        if (newPage >= 0 && newPage < totalPages) {
         currentPage = newPage;
         loadPage();
    }
    }

    private void showResult() {
        for (int i = 0; i < answers.length; i++) {
            if (answers[i] == -1) {
                JOptionPane.showMessageDialog(this, "Please answer all questions before seeing the result.");
                return;
            }
        }

        int score = 0;
        for (int answer : answers) score += answer;

        String category;
        String suggestion;

        if (score <= 10) {
            category = "Normal";
            suggestion = "You’re doing okay.";
        } else if (score <= 20) {
            category = "Mild";
            suggestion = "Monitor your symptoms.";
        } else if (score <= 30) {
            category = "Moderate";
            suggestion = "Consider support.";
        } else {
            category = "Severe";
            suggestion = "Consult an expert.";
        }

        // If user is registered, save result to DB
        if (isRegisteredUser) {
            int uid = SessionManager.getCurrentUserId();
            if (uid <= 0) {
                // try to warn and abort saving, but show result UI anyway
                JOptionPane.showMessageDialog(this, "You must be logged in to save the test result. The result will not be saved.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            } else {
                saveTestResultToDB(uid, disorder, score, category, suggestion);
            }
        }else {
        // Guest user → ask if they want to register
        int choice = JOptionPane.showConfirmDialog(this,
            "Guests cannot save results.\nWould you like to register now?",
            "Registration Required",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            new HomePage(); // 👈 open your registration form
            dispose(); // close current test page
            return; // don’t go to ResultPage until registered
        }
    }

        dispose();
        new ResultPage(disorder, score, category, suggestion, isRegisteredUser);
    }

    private void saveTestResultToDB(int userId, String disorderName, int score, String category, String suggestion) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DB.getConnection();

            // 1) find test_id for disorderName (insert if missing)
            int testId = -1;
            String findSql = "SELECT test_id FROM mood_tests WHERE disorder_name = ?";
            ps = con.prepareStatement(findSql);
            ps.setString(1, disorderName);
            rs = ps.executeQuery();
            if (rs.next()) {
                testId = rs.getInt(1);
                rs.close();
                ps.close();
            } else {
                if (rs != null) { rs.close(); rs = null; }
                if (ps != null) { ps.close(); ps = null; }
                String insTest = "INSERT INTO mood_tests (disorder_name, description) VALUES (?, ?)";
                ps = con.prepareStatement(insTest, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, disorderName);
                ps.setString(2, disorderName + " test");
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    testId = rs.getInt(1);
                }
                rs.close();
                ps.close();
            }

            // 2) insert into mood_test_results
            String insertResult = "INSERT INTO mood_test_results (user_id, test_id, score, category, suggestion) VALUES (?, ?, ?, ?, ?)";
            ps = con.prepareStatement(insertResult);
            ps.setInt(1, userId);
            ps.setInt(2, testId);
            ps.setInt(3, score);
            ps.setString(4, category);
            ps.setString(5, suggestion);
            ps.executeUpdate();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save test result: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }

    public static class Question {
        public final String text;
        public final String[] options;

        public Question(String text, String[] options) {
            this.text = text;
            this.options = options;
        }
    }

    public static class QuestionFactory {
        public static List<Question> getQuestions(String disorder) {
            List<Question> list = new ArrayList<>();
            String[] scaleDefault = {"Not at all (0)", "Several days (1)", "More than half the days (2)", "Nearly every day (3)"};
            String[] scaleSimple = {"Never", "Sometimes", "Often", "Always"};
            String[] scaleAdjust = {"Not at all", "A little", "Moderately", "Extremely"};
            String[] scaleOccasion = {"Not at all", "Occasionally", "Often", "Almost always"};
            String[] scaleAdhd = {"Never", "Sometimes", "Often", "Very Often"};

            switch (disorder) {
                case "OCD":
                    Collections.addAll(list, new Question[] {
                        new Question("I have repeated unwanted thoughts or images I can’t control.", scaleDefault),
                        new Question("I feel the need to wash my hands or clean repeatedly.", scaleDefault),
                        new Question("I double-check things like locks, appliances, or doors often.", scaleDefault),
                        new Question("I arrange or order things in a specific way to feel right.", scaleDefault),
                        new Question("I avoid objects or places that may trigger obsessions.", scaleDefault),
                        new Question("I repeat words or actions to prevent bad things from happening.", scaleDefault),
                        new Question("I feel anxious if I don’t perform a ritual.", scaleDefault),
                        new Question("I spend more than an hour a day on obsessive thoughts or compulsions.", scaleDefault),
                        new Question("I am aware my behaviors are unreasonable but can’t stop them.", scaleDefault),
                        new Question("I get upset when my routine is disrupted.", scaleDefault),
                        new Question("My compulsive behaviors interfere with my social life or work.", scaleDefault),
                        new Question("I feel embarrassed about my behaviors or rituals.", scaleDefault),
                        new Question("I feel responsible for preventing harm by doing rituals.", scaleDefault),
                        new Question("I try to suppress obsessive thoughts but fail.", scaleDefault),
                        new Question("I feel my life is limited by my compulsions.", scaleDefault)
                    });
                    break;

                case "MDD":
                    Collections.addAll(list, new Question[] {
                        new Question("I have little interest or pleasure in doing things.", scaleDefault),
                        new Question("I feel down, depressed, or hopeless.", scaleDefault),
                        new Question("I sleep too much or too little.", scaleDefault),
                        new Question("I feel tired or lack energy.", scaleDefault),
                        new Question("I overeat or have no appetite.", scaleDefault),
                        new Question("I feel bad about myself or feel like a failure.", scaleDefault),
                        new Question("I have trouble concentrating.", scaleDefault),
                        new Question("I feel like moving or speaking slowly.", scaleDefault),
                        new Question("I feel restless or agitated.", scaleDefault),
                        new Question("I cry more than usual.", scaleDefault),
                        new Question("I avoid social activities.", scaleDefault),
                        new Question("I have thoughts of harming myself.", scaleDefault),
                        new Question("I feel worthless or guilty.", scaleDefault),
                        new Question("I lose interest in personal hygiene.", scaleDefault),
                        new Question("I feel like things will never get better.", scaleDefault)
                    });
                    break;

                case "Adjustment Disorder":
                    Collections.addAll(list, new Question[] {
                        new Question("I’ve experienced a significant life change recently.", scaleAdjust),
                        new Question("I feel overwhelmed by this change.", scaleAdjust),
                        new Question("I am constantly thinking about the event.", scaleAdjust),
                        new Question("I have emotional outbursts due to the situation.", scaleAdjust),
                        new Question("I feel disconnected from others.", scaleAdjust),
                        new Question("I struggle with motivation.", scaleAdjust),
                        new Question("I have physical symptoms (headaches, stomach issues).", scaleAdjust),
                        new Question("I avoid activities that remind me of the change.", scaleAdjust),
                        new Question("I feel like crying for no reason.", scaleAdjust),
                        new Question("I can’t focus on daily tasks.", scaleAdjust),
                        new Question("I feel numb.", scaleAdjust),
                        new Question("I feel guilty for how I’ve reacted.", scaleAdjust),
                        new Question("I feel hopeless about adjusting to the change.", scaleAdjust),
                        new Question("I lash out at loved ones.", scaleAdjust),
                        new Question("I find no joy in things I used to enjoy.", scaleAdjust)
                    });
                    break;

                case "Insomnia":
                    Collections.addAll(list, new Question[] {
                        new Question("I struggle to fall asleep within 30 minutes.", scaleSimple),
                        new Question("I wake up several times at night.", scaleSimple),
                        new Question("I wake up too early and can't return to sleep.", scaleSimple),
                        new Question("I feel tired even after sleep.", scaleSimple),
                        new Question("I worry about getting enough sleep.", scaleSimple),
                        new Question("I use devices in bed for hours.", scaleSimple),
                        new Question("I need naps to get through the day.", scaleSimple),
                        new Question("I rely on medication or substances to sleep.", scaleSimple),
                        new Question("I avoid evening plans due to fatigue.", scaleSimple),
                        new Question("I find myself yawning frequently during the day.", scaleSimple),
                        new Question("I feel anxious as bedtime approaches.", scaleSimple),
                        new Question("I find it hard to focus after a poor night of sleep.", scaleSimple),
                        new Question("I have vivid dreams or nightmares frequently.", scaleSimple),
                        new Question("I cancel activities because of sleep issues.", scaleSimple),
                        new Question("I feel frustrated when I can’t sleep.", scaleSimple)
                    });
                    break;

                case "Schizophrenia":
                    Collections.addAll(list, new Question[] {
                        new Question("I hear voices when no one is there.", scaleOccasion),
                        new Question("I see things others don't.", scaleOccasion),
                        new Question("I believe people are watching or following me.", scaleOccasion),
                        new Question("I feel my thoughts are not my own.", scaleOccasion),
                        new Question("I experience sudden paranoia.", scaleOccasion),
                        new Question("I have disorganized or jumbled thoughts.", scaleOccasion),
                        new Question("I have difficulty distinguishing dreams from reality.", scaleOccasion),
                        new Question("I isolate myself from others.", scaleOccasion),
                        new Question("I feel emotionally flat or unresponsive.", scaleOccasion),
                        new Question("I have trouble understanding what people are saying.", scaleOccasion),
                        new Question("I believe I have special powers or a unique mission.", scaleOccasion),
                        new Question("I forget things or lose track of conversations easily.", scaleOccasion),
                        new Question("I speak in a way that others don’t understand.", scaleOccasion),
                        new Question("I feel that my body is being controlled.", scaleOccasion),
                        new Question("I don’t recognize my own reflection sometimes.", scaleOccasion)
                    });
                    break;

                case "Anxiety":
                    Collections.addAll(list, new Question[] {
                        new Question("I feel nervous or restless.", scaleDefault),
                        new Question("I worry excessively about daily things.", scaleDefault),
                        new Question("I avoid situations that make me anxious.", scaleDefault),
                        new Question("I get tired easily due to worrying.", scaleDefault),
                        new Question("I have muscle tension.", scaleDefault),
                        new Question("I feel something terrible might happen.", scaleDefault),
                        new Question("I avoid speaking in public or crowds.", scaleDefault),
                        new Question("I have trouble concentrating due to worry.", scaleDefault),
                        new Question("I experience racing thoughts.", scaleDefault),
                        new Question("I feel overwhelmed by responsibilities.", scaleDefault),
                        new Question("I feel panic or dread without cause.", scaleDefault),
                        new Question("I fear being judged.", scaleDefault),
                        new Question("I sweat or tremble during anxiety.", scaleDefault),
                        new Question("I try to control everything around me.", scaleDefault),
                        new Question("I experience stomach issues when anxious.", scaleDefault)
                    });
                    break;

                case "Panic Disorder":
                    Collections.addAll(list, new Question[] {
                        new Question("I have sudden episodes of intense fear.", scaleSimple),
                        new Question("I fear losing control during these episodes.", scaleSimple),
                        new Question("I avoid crowded places.", scaleSimple),
                        new Question("My heart races for no reason.", scaleSimple),
                        new Question("I feel dizzy or lightheaded during fear.", scaleSimple),
                        new Question("I worry about when the next panic will happen.", scaleSimple),
                        new Question("I feel detached from my body.", scaleSimple),
                        new Question("I fear dying during an episode.", scaleSimple),
                        new Question("I have chest pain or discomfort during panic.", scaleSimple),
                        new Question("I avoid physical activity fearing symptoms.", scaleSimple),
                        new Question("I carry medications or items for 'just in case' situations.", scaleSimple),
                        new Question("I experience shaking or trembling suddenly.", scaleSimple),
                        new Question("I feel the urge to flee a situation.", scaleSimple),
                        new Question("I hyperventilate or gasp for air.", scaleSimple),
                        new Question("I cannot calm down quickly after panic.", scaleSimple)
                    });
                    break;

                case "ADHD":
                    Collections.addAll(list, new Question[] {
                        new Question("I struggle to pay attention during conversations.", scaleAdhd),
                        new Question("I get easily distracted by noises or movements.", scaleAdhd),
                        new Question("I interrupt others in conversations.", scaleAdhd),
                        new Question("I have trouble completing long-term tasks.", scaleAdhd),
                        new Question("I forget important dates or appointments.", scaleAdhd),
                        new Question("I often lose items like keys or phone.", scaleAdhd),
                        new Question("I struggle with organizing tasks.", scaleAdhd),
                        new Question("I procrastinate on important responsibilities.", scaleAdhd),
                        new Question("I feel restless when sitting still.", scaleAdhd),
                        new Question("I talk more than others expect me to.", scaleAdhd),
                        new Question("I act without thinking about the consequences.", scaleAdhd),
                        new Question("I avoid tasks requiring focus.", scaleAdhd),
                        new Question("I jump between hobbies quickly.", scaleAdhd),
                        new Question("I am impulsive when spending money.", scaleAdhd),
                        new Question("I miss deadlines even with reminders.", scaleAdhd)
                    });
                    break;
            }
            return list;
        }
    }
}
