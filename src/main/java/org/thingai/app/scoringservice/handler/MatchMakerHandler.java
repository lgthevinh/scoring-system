package org.thingai.app.scoringservice.handler;

import org.thingai.base.log.ILog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MatchMakerHandler {
    private static final String TAG = "MatchMakerHandler";
    private String binPath;
    private String outPath;

    public void setBinPath(String path) {
        this.binPath = path;
        ILog.d(TAG, "Binary path set to: " + this.binPath);
    }

    public String getBinPath() {
        return binPath;
    }

    public void setOutPath(String path) {
        this.outPath = path;
        ILog.d(TAG, "Output path set to: " + this.outPath);
    }

    public String getOutPath() {
        return outPath;
    }

    /**
     * Run the external match maker tool and log its output.
     * If outPath is a directory, uses it as CWD.
     * If outPath is a file, uses its parent as CWD and tees stdout to that file.
     *
     * @return exit code (0 = success)
     */
    public int generateMatchSchedule(int rounds, int numberOfTeams, int allianceSize) {
        if (binPath == null || binPath.isBlank() || outPath == null || outPath.isBlank()) {
            ILog.e(TAG, "Binary path or output path is not set.");
            return -1;
        }

        try {
            Path bin = Paths.get(binPath).toAbsolutePath().normalize();
            if (!Files.exists(bin) || !Files.isRegularFile(bin)) {
                ILog.e(TAG, "Binary not found or not a file: " + bin);
                return -2;
            }
            // Ensure executable on POSIX (ignored on Windows)
            tryEnsureExecutable(bin);

            Path out = Paths.get(outPath).toAbsolutePath().normalize();

            // Determine working directory and optional output file (if out looks like a file)
            Path workingDir;
            Path stdoutFile;

            if (Files.exists(out) && Files.isDirectory(out)) {
                stdoutFile = null;
                // Existing directory
                workingDir = out;
            } else {
                // If path ends with a filename (e.g., *.txt), treat parent as working dir
                // Create parent directories if needed
                String fileName = out.getFileName() != null ? out.getFileName().toString() : "";
                boolean looksLikeFile = fileName.contains(".") || fileName.endsWith(".txt");
                if (looksLikeFile) {
                    Path parent = out.getParent();
                    if (parent == null) {
                        ILog.e(TAG, "Output path looks like a file but has no parent directory: " + out);
                        return -3;
                    }
                    Files.createDirectories(parent);
                    workingDir = parent;
                    stdoutFile = out; // tee stdout to this file
                } else {
                    stdoutFile = null;
                    // Treat as directory path that doesn't exist yet
                    Files.createDirectories(out);
                    workingDir = out;
                }
            }

            // Build command
            List<String> cmd = new ArrayList<>();
            cmd.add(bin.toString());
            cmd.add("-t"); cmd.add(String.valueOf(numberOfTeams));
            cmd.add("-r"); cmd.add(String.valueOf(rounds));
            cmd.add("-a"); cmd.add(String.valueOf(allianceSize));

            // If your CLI supports explicit output arg, prefer this:
            // if (stdoutFile != null) {
            //     cmd.add("-o");
            //     cmd.add(stdoutFile.toString());
            // }

            ILog.d(TAG, "Working directory: " + workingDir);
            ILog.d(TAG, "Executing: " + String.join(" ", cmd));
            if (stdoutFile != null) {
                ILog.d(TAG, "Stdout will be written to: " + stdoutFile);
            }

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(workingDir.toFile());
            pb.redirectErrorStream(true); // merge stderr into stdout

            Process process = pb.start();

            // Stream output to logs and optionally to file
            Thread outThread = new Thread(() -> streamToLogAndOptionalFile(process.getInputStream(), stdoutFile));
            outThread.setName("matchmaker-stdout");
            outThread.setDaemon(true);
            outThread.start();

            boolean finished = process.waitFor(300, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                ILog.e(TAG, "Match maker timed out and was terminated.");
                return -4;
            }

            int exitCode = process.exitValue();
            ILog.d(TAG, "Match maker finished with exit code: " + exitCode);
            return exitCode;
        } catch (IOException e) {
            ILog.e(TAG, "IO error running match maker: " + e.getMessage());
            return -5;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ILog.e(TAG, "Interrupted while waiting for match maker: " + e.getMessage());
            return -6;
        }
    }

    private void streamToLogAndOptionalFile(InputStream inputStream, Path optionalFile) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             BufferedWriter writer = optionalFile != null
                     ? Files.newBufferedWriter(optionalFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                     : null) {
            String line;
            while ((line = reader.readLine()) != null) {
                ILog.d(TAG, "[proc] " + line);
                if (writer != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            if (writer != null) writer.flush();
        } catch (IOException e) {
            ILog.w(TAG, "Failed to stream process output: " + e.getMessage());
        }
    }

    private void tryEnsureExecutable(Path bin) {
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(bin);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(bin, perms);
        } catch (UnsupportedOperationException ignored) {
            // Windows or non-POSIX FS; ignore
        } catch (IOException ex) {
            ILog.w(TAG, "Unable to adjust executable permissions: " + ex.getMessage());
        }
    }
}