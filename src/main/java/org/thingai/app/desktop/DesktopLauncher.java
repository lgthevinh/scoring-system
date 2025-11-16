package org.thingai.app.desktop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class    DesktopLauncher {

    private JFrame frame;
    private JLabel statusLabel;
    private JLabel urlLabel;
    private JLabel dbPathLabel;
    private JLabel logPathLabel;
    private JButton openBtn;
    private JButton exitBtn;

    private Process serverProcess;
    private File baseDir;      // distribution root (where bin/data/app live)
    private File appDir;       // .../app
    private File dataDir;      // .../data
    private File logsDir;      // .../logs
    private File bootJar;      // .../app/scoring-system.jar
    private File logFile;      // logs/scoring-YYYYMMDD-HHMMSS.log
    private String serverUrl;  // http://<ip>:9090

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DesktopLauncher launcher = new DesktopLauncher();
            launcher.createAndShow();
            launcher.startServerAsync();
        });
    }

    private void createAndShow() {
        resolveLayoutPaths();
        serverUrl = "http://" + resolveLocalIPv4() + ":9090";

        frame = new JFrame("Live Scoring System");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(700, 520);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)); // placeholder

        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("<html><div style='text-align:center;font-size:20px;font-weight:bold;'>FIRST Tech Challenge Live Scoring System</div></html>", SwingConstants.CENTER);
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createVerticalStrut(10));

        statusLabel = new JLabel("Starting server...", SwingConstants.LEFT);

        // UPDATED: pass Runnable lambdas; helper wraps with MouseAdapter
        urlLabel = hyperlinkLabel(serverUrl, () -> openInBrowser(serverUrl));
        dbPathLabel = hyperlinkLabel(pathString(dataDir), () -> openFile(dataDir));
        logPathLabel = hyperlinkLabel("Pending...", () -> { if (logFile != null) openFile(logFile.getParentFile()); });

        center.add(line("Scoring system started! Version: " + safeVersion()));
        center.add(Box.createVerticalStrut(8));
        center.add(line("URL: ", urlLabel));
        center.add(Box.createVerticalStrut(8));
        center.add(line("Databases stored in: ", dbPathLabel));
        center.add(Box.createVerticalStrut(8));
        center.add(line("Logs stored in: ", logPathLabel));
        center.add(Box.createVerticalStrut(12));
        center.add(statusLabel);

        root.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        openBtn = new JButton("Open in Browser");
        openBtn.addActionListener(e -> openInBrowser(serverUrl));
        exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> {
            stopServer();
            frame.dispose();
            System.exit(0);
        });
        buttons.add(openBtn);
        buttons.add(exitBtn);
        root.add(buttons, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                stopServer();
                frame.dispose();
                System.exit(0);
            }
        });
    }

    private void startServerAsync() {
        new Thread(() -> {
            try {
                if (!bootJar.exists()) {
                    setStatus("Error: Boot jar not found: " + bootJar.getAbsolutePath());
                    return;
                }
                if (!logsDir.exists()) Files.createDirectories(logsDir.toPath());
                String ts = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
                logFile = logsDir.toPath().resolve("scoring-" + ts + ".log").toFile();
                logPathLabel.setText(logFile.getAbsolutePath());

                String javaBin = Path.of(System.getProperty("java.home"), "bin", isWindows() ? "java.exe" : "java").toString();

                ProcessBuilder pb = new ProcessBuilder(
                        javaBin,
                        "-jar",
                        bootJar.getAbsolutePath()
                );
                pb.directory(baseDir);
                pb.redirectErrorStream(true);
                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

                serverProcess = pb.start();
                setStatus("Server starting... Logs: " + logFile.getAbsolutePath());

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
        } catch (Exception ignored) {}
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
            bootJar = new File(appDir, "scoring-system.jar");
        }
    }

    private JPanel line(String labelText) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(labelText));
        return p;
    }

    private JPanel line(String labelText, JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(labelText));
        p.add(comp);
        return p;
    }

    // UPDATED: accept Runnable and wrap with MouseAdapter to avoid MouseListener lambda error
    private JLabel hyperlinkLabel(String text, Runnable onClick) {
        JLabel l = new JLabel("<html><a href='#'>" + escapeHtml(text) + "</a></html>");
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });
        return l;
    }

    private void openInBrowser(String url) {
        try { Desktop.getDesktop().browse(new URI(url)); }
        catch (Exception ex) { setStatus("Open browser failed: " + ex.getMessage()); }
    }

    private void openFile(File f) {
        try { Desktop.getDesktop().open(f); }
        catch (Exception ex) { setStatus("Open file failed: " + ex.getMessage()); }
    }

    private void setStatus(String s) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(s));
    }

    private String resolveLocalIPv4() {
        try { return InetAddress.getLocalHost().getHostAddress(); }
        catch (Exception e) { return "127.0.0.1"; }
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