package com.example.shareupload;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;

public class ServerUI extends JFrame {

    private ConfigurableApplicationContext context;
    private JTextField folderField;
    private JButton startStopButton;
    private JButton browseButton;
    private JButton openBrowserButton;
    private JLabel statusLabel;
    private boolean isRunning = false;
    private int serverPort = 8080;

    public ServerUI() {
        setTitle("Share Files HTTP Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 180);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Folder selection panel
        JPanel folderPanel = new JPanel(new BorderLayout(5, 0));
        JLabel folderLabel = new JLabel("Upload folder:");
        folderField = new JTextField(System.getProperty("user.home") + "/Downloads");
        browseButton = new JButton("Browse...");

        folderPanel.add(folderLabel, BorderLayout.WEST);
        folderPanel.add(folderField, BorderLayout.CENTER);
        folderPanel.add(browseButton, BorderLayout.EAST);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        startStopButton = new JButton("▶ Start");
        startStopButton.setPreferredSize(new Dimension(120, 30));
        startStopButton.setFont(startStopButton.getFont().deriveFont(Font.BOLD));

        openBrowserButton = new JButton("🌐 Open Browser");
        openBrowserButton.setEnabled(false);

        statusLabel = new JLabel("Server stopped", SwingConstants.CENTER);
        statusLabel.setForeground(Color.GRAY);

        controlPanel.add(startStopButton);
        controlPanel.add(openBrowserButton);
        controlPanel.add(statusLabel);

        mainPanel.add(folderPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Event listeners
        browseButton.addActionListener(e -> browseFolder());
        startStopButton.addActionListener(e -> toggleServer());
        openBrowserButton.addActionListener(e -> openBrowser());
    }

    private void openBrowser() {
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:" + serverPort));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not open browser: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void browseFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(folderField.getText()));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            folderField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void toggleServer() {
        if (isRunning) {
            stopServer();
        } else {
            startServer();
        }
    }

    private void startServer() {
        String uploadPath = folderField.getText();
        File folder = new File(uploadPath);

        if (!folder.exists() || !folder.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a valid folder",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        startStopButton.setEnabled(false);
        browseButton.setEnabled(false);
        folderField.setEnabled(false);
        statusLabel.setText("Starting...");

        new Thread(() -> {
            try {
                System.setProperty("uploadPath", uploadPath);
                context = SpringApplication.run(ShareUploadApplication.class);
                serverPort = context.getEnvironment().getProperty("server.port", Integer.class, 8080);

                SwingUtilities.invokeLater(() -> {
                    isRunning = true;
                    startStopButton.setText("■ Stop");
                    startStopButton.setEnabled(true);
                    openBrowserButton.setEnabled(true);
                    statusLabel.setText("Running on port " + serverPort);
                    statusLabel.setForeground(new Color(76, 175, 80));
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    startStopButton.setEnabled(true);
                    browseButton.setEnabled(true);
                    folderField.setEnabled(true);
                    statusLabel.setText("Failed to start");
                    statusLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(ServerUI.this,
                            "Failed to start server: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void stopServer() {
        startStopButton.setEnabled(false);
        statusLabel.setText("Stopping...");

        new Thread(() -> {
            try {
                if (context != null) {
                    context.close();
                    context = null;
                }

                SwingUtilities.invokeLater(() -> {
                    isRunning = false;
                    startStopButton.setText("▶ Start");
                    startStopButton.setEnabled(true);
                    openBrowserButton.setEnabled(false);
                    browseButton.setEnabled(true);
                    folderField.setEnabled(true);
                    statusLabel.setText("Server stopped");
                    statusLabel.setForeground(Color.GRAY);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    startStopButton.setEnabled(true);
                    statusLabel.setText("Error stopping");
                    statusLabel.setForeground(Color.RED);
                });
            }
        }).start();
    }

    public static void launch() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            ServerUI ui = new ServerUI();
            ui.setVisible(true);
        });
    }
}
