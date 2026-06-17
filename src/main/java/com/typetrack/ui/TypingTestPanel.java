package com.typetrack.ui;

import com.typetrack.engine.TypingEngine;
import com.typetrack.engine.TypingTest;
import com.typetrack.model.Result;
import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Screen where the user performs the typing test.
 * Displays target text and records user keystrokes with live metric feedback.
 */
public class TypingTestPanel extends JPanel {
    private final MainFrame mainFrame;
    private final String targetText;
    private final TypingTest test;

    // Swing Components
    private JTextArea areaTarget;
    private JTextArea areaInput;
    private JLabel lblTimer;
    private JLabel lblMistakes;
    private JLabel lblAccuracy;

    // Timer state
    private Timer uiTimer;
    private int elapsedSeconds = 0;
    private boolean testStarted = false;
    private final TypingEngine liveCalculator; // for live stats tracking

    public TypingTestPanel(MainFrame mainFrame, String targetText) {
        this.mainFrame = mainFrame;
        this.targetText = targetText;
        this.test = new TypingTest(mainFrame.getCurrentUser(), targetText);
        this.liveCalculator = new TypingEngine(targetText);

        setBackground(UIStyle.COLOR_BG);
        setLayout(new BorderLayout());
        setBorder(UIStyle.BORDER_PADDING);

        initializeUI();
        setupTimer();
    }

    private void initializeUI() {
        // --- TOP BAR (Title & Reset) ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIStyle.COLOR_BG);

        JLabel lblHeader = ComponentsHelper.createTitleLabel("Typing Speed Test");
        topBar.add(lblHeader, BorderLayout.WEST);

        // Right header containing theme toggle and quit buttons
        JPanel rightTop = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        rightTop.setBackground(UIStyle.COLOR_BG);

        rightTop.add(ComponentsHelper.createThemeToggleButton(mainFrame));

        JButton btnQuit = ComponentsHelper.createSecondaryButton("Quit to Dashboard");
        btnQuit.addActionListener(e -> {
            stopTimerIfRunning();
            mainFrame.showPanel("Dashboard");
        });
        rightTop.add(btnQuit);

        topBar.add(rightTop, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // --- CENTER AREA (Text fields) ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setBackground(UIStyle.COLOR_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // 1. Target Area (Read-Only)
        areaTarget = new JTextArea(targetText);
        areaTarget.setFont(UIStyle.FONT_MONO);
        areaTarget.setLineWrap(true);
        areaTarget.setWrapStyleWord(true);
        areaTarget.setEditable(false);
        areaTarget.setBackground(UIStyle.COLOR_CARD);
        areaTarget.setForeground(UIStyle.COLOR_MUTED);
        areaTarget.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollTarget = new JScrollPane(areaTarget);
        scrollTarget.setBorder(BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1));
        centerPanel.add(scrollTarget);

        // 2. Input Area (Active)
        areaInput = new JTextArea();
        areaInput.setFont(UIStyle.FONT_MONO);
        areaInput.setLineWrap(true);
        areaInput.setWrapStyleWord(true);
        areaInput.setBackground(UIStyle.COLOR_CARD);
        areaInput.setForeground(UIStyle.COLOR_TEXT);
        areaInput.setCaretColor(UIStyle.COLOR_TEXT);
        areaInput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollInput = new JScrollPane(areaInput);
        scrollInput.setBorder(BorderFactory.createLineBorder(UIStyle.COLOR_ACCENT, 1));
        centerPanel.add(scrollInput);

        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM BAR (Stats & Finish button) ---
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(UIStyle.COLOR_BG);

        // Stats Panel (Left)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        statsPanel.setBackground(UIStyle.COLOR_BG);

        lblTimer = new JLabel("Time: 0s");
        lblTimer.setFont(UIStyle.FONT_BODY_BOLD);
        lblTimer.setForeground(UIStyle.COLOR_TEXT);
        statsPanel.add(lblTimer);

        lblMistakes = new JLabel("Mistakes: 0");
        lblMistakes.setFont(UIStyle.FONT_BODY_BOLD);
        lblMistakes.setForeground(UIStyle.COLOR_DANGER);
        statsPanel.add(lblMistakes);

        lblAccuracy = new JLabel("Accuracy: 100.0%");
        lblAccuracy.setFont(UIStyle.FONT_BODY_BOLD);
        lblAccuracy.setForeground(UIStyle.COLOR_SUCCESS);
        statsPanel.add(lblAccuracy);

        bottomBar.add(statsPanel, BorderLayout.WEST);

        // Actions Panel (Right)
        JButton btnFinish = ComponentsHelper.createButton("Finish Test");
        btnFinish.addActionListener(e -> finishTestFlow());
        bottomBar.add(btnFinish, BorderLayout.EAST);

        add(bottomBar, BorderLayout.SOUTH);

        // --- EVENT HANDLERS ---
        areaInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleTyping();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleTyping();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleTyping();
            }
        });
    }

    private void setupTimer() {
        // UI Timer fires every 1 second to update elapsed seconds label
        uiTimer = new Timer(1000, e -> {
            elapsedSeconds++;
            lblTimer.setText("Time: " + elapsedSeconds + "s");
        });
    }

    private void stopTimerIfRunning() {
        if (uiTimer != null && uiTimer.isRunning()) {
            uiTimer.stop();
        }
    }

    private void handleTyping() {
        String typedText = areaInput.getText();

        // 1. Auto-start test on first key stroke
        if (!testStarted && !typedText.isEmpty()) {
            testStarted = true;
            test.start();
            liveCalculator.start();
            uiTimer.start();
        }

        // 2. Perform live calculations for UI feedback
        if (testStarted) {
            liveCalculator.calculateResults(typedText);
            lblMistakes.setText("Mistakes: " + liveCalculator.getMistakes());
            lblAccuracy.setText(String.format("Accuracy: %.1f%%", liveCalculator.getAccuracy()));
        }

        // 3. Auto-finish if user typed the length of the paragraph
        if (typedText.length() >= targetText.length()) {
            finishTestFlow();
        }
    }

    private void finishTestFlow() {
        stopTimerIfRunning();

        // Prevent finishing if the test never even started (no text typed)
        if (!testStarted) {
            mainFrame.showPanel("Dashboard");
            return;
        }

        String finalTypedText = areaInput.getText();
        Result result = test.finish(finalTypedText);

        // Persist the result to database & cache
        mainFrame.getStatsManager().addResult(result, mainFrame.getCurrentUser().getUserId());

        // Redirect to ResultsPanel with dynamic results
        ResultsPanel resultsPanel = new ResultsPanel(mainFrame, result);
        mainFrame.registerDynamicPanel(resultsPanel, "Results");
        mainFrame.showPanel("Results");
    }
}
