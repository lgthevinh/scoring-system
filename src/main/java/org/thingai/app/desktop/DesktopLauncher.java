package org.thingai.app.desktop;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DesktopLauncher {

    private JFrame frame;
    private JLabel statusLabel;
    private JLabel urlLabel;
    private JLabel dbPathLabel;
    private JLabel logPathLabel;
    private JButton openBtn;
    private JButton exitBtn;

    private Process serverProcess;
    private File baseDir; // distribution root (where bin/data/app live)
    private File appDir; // .../app
    private File dataDir; // .../data
    private File logsDir; // .../logs
    private File bootJar; // .../app/scoring-system.jar
    private File logFile; // logs/scoring-YYYYMMDD-HHMMSS.log
    private String serverUrl; // http://<ip>:9090

    public static void main(String[] args) {
        // Setup FlatLaf
        try {
            if (isDarkSystem()) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            DesktopLauncher launcher = new DesktopLauncher();
            launcher.createAndShow();
            launcher.startServerAsync();
        });
    }

    private static boolean isDarkSystem() {
        // Simple heuristic or preference check could go here
        // For now, default to light unless we want to force dark
        return false;
    }

    private void createAndShow() {
        resolveLayoutPaths();
        serverUrl = "http://" + resolveLocalIPv4();

        frame = new JFrame("Live Scoring System");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(750, 550);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)); // placeholder

        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel title = new JLabel("Live Scoring System", SwingConstants.CENTER);
        title.putClientProperty("FlatLaf.styleClass", "h1");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        root.add(title, BorderLayout.NORTH);

        // Center Content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(20, 0, 20, 0));

        statusLabel = new JLabel("Starting server...", SwingConstants.LEFT);
        statusLabel.putClientProperty("FlatLaf.styleClass", "large");

        urlLabel = hyperlinkLabel(serverUrl, () -> openInBrowser(serverUrl));
        dbPathLabel = hyperlinkLabel(pathString(dataDir), () -> openFile(dataDir));
        logPathLabel = hyperlinkLabel("Pending...", () -> {
            if (logFile != null)
                openFile(logFile.getParentFile());
        });

        center.add(createInfoPanel("Status", statusLabel));
        center.add(Box.createVerticalStrut(2));
        center.add(createInfoPanel("Server URL", urlLabel));
        center.add(Box.createVerticalStrut(2));
        center.add(createInfoPanel("Database Path", dbPathLabel));
        center.add(Box.createVerticalStrut(2));
        center.add(createInfoPanel("Logs Path", logPathLabel));
        center.add(Box.createVerticalStrut(20));

        JLabel versionLabel = new JLabel("Version: " + safeVersion());
        versionLabel.putClientProperty("FlatLaf.styleClass", "small");
        center.add(versionLabel);

        root.add(center, BorderLayout.CENTER);

        // Footer Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        openBtn = new JButton("Open in Browser");
        openBtn.putClientProperty("JButton.buttonType", "roundRect");
        openBtn.addActionListener(e -> openInBrowser(serverUrl));
        openBtn.setEnabled(false); // Disabled until server starts

        JButton openLogsBtn = new JButton("Open Logs Folder");
        openLogsBtn.addActionListener(e -> openFile(logsDir));

        exitBtn = new JButton("Stop & Exit");
        exitBtn.addActionListener(e -> {
            stopServer();
            frame.dispose();
            System.exit(0);
        });

        buttons.add(openBtn);
        buttons.add(openLogsBtn);
        buttons.add(exitBtn);
        root.add(buttons, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopServer();
                frame.dispose();
                System.exit(0);
            }
        });
    }

    private JPanel createInfoPanel(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        JLabel titleLbl = new JLabel(title + ":");
        titleLbl.setFont(titleLbl.getFont().deriveFont(Font.BOLD));
        titleLbl.setPreferredSize(new Dimension(100, 20));
        p.add(titleLbl, BorderLayout.WEST);
        p.add(content, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return p;
    }

    private void startServerAsync() {
        new Thread(() -> {
            try {
                if (!bootJar.exists()) {
                    setStatus("Error: Boot jar not found: " + bootJar.getAbsolutePath());
                    return;
                }
                if (!logsDir.exists())
                    Files.createDirectories(logsDir.toPath());
                String ts = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
                logFile = logsDir.toPath().resolve("scoring-" + ts + ".log").toFile();

                SwingUtilities.invokeLater(() -> logPathLabel.setText(logFile.getAbsolutePath()));

                String javaBin = Path.of(System.getProperty("java.home"), "bin", isWindows() ? "java.exe" : "java")
                        .toString();

                ProcessBuilder pb = new ProcessBuilder(
                        javaBin,
                        "-jar",
                        bootJar.getAbsolutePath());
                pb.directory(baseDir);
                pb.redirectErrorStream(true);
                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

                serverProcess = pb.start();
                setStatus("Server is running");

                SwingUtilities.invokeLater(() -> {
                    openBtn.setEnabled(true);
                    urlLabel.setEnabled(true);
                });
            } catch (Exception ex) {
                setStatus("Failed to start server: " + ex.getMessage());
                ex.printStackTrace();
            }
        }, "server-starter").start();
    }

    private void stopServer() {
        try {
            if (serverProcess != null && serverProcess.isAlive()) {
                serverProcess.destroy();
                serverProcess.waitFor();
            }
        } catch (Exception ignored) {
        }
    }

    private void resolveLayoutPaths() {
        try {
            File where = new File(DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File jarDir = where.getParentFile();
            if (jarDir != null && jarDir.getName().equalsIgnoreCase("app")) {
                baseDir = jarDir.getParentFile();
            } else {
                baseDir = new File(System.getProperty("user.dir"));
            }

            appDir = new File(baseDir, "app");
            dataDir = new File(baseDir, "data");
            logsDir = new File(baseDir, "logs");
            bootJar = new File(appDir, "scoring-system.jar");
        } catch (Exception e) {
            baseDir = new File(System.getProperty("user.dir"));
            appDir = new File(baseDir, "app");
            dataDir = new File(baseDir, "data");
            logsDir = new File(baseDir, "logs");
            bootJar = new File("scoring-system.jar");
        }
    }

    private JLabel hyperlinkLabel(String text, Runnable onClick) {
        JLabel l = new JLabel("<html><a href='#'>" + escapeHtml(text) + "</a></html>");
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null)
                    onClick.run();
            }
        });
        return l;
    }

    private void openInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            setStatus("Open browser failed: " + ex.getMessage());
        }
    }

    private void openFile(File f) {
        try {
            Desktop.getDesktop().open(f);
        } catch (Exception ex) {
            setStatus("Open file failed: " + ex.getMessage());
        }
    }

    private void setStatus(String s) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(s));
    }

    private String resolveLocalIPv4() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static String pathString(File f) {
        return f == null ? "-" : f.getAbsolutePath();
    }

    private static String escapeHtml(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String safeVersion() {
        try {
            String v = DesktopLauncher.class.getPackage().getImplementationVersion();
            return v != null ? v : "dev";
        } catch (Exception e) {
            return "dev";
        }
    }
}
