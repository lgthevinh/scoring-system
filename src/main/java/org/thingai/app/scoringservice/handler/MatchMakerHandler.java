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
     * Generate match schedule using Java implementation instead of external executable.
     * This replaces the missing MatchMaker.exe with a Java-based scheduler.
     */
    public int generateMatchSchedule(int rounds, int numberOfTeams, int allianceSize) {
        if (outPath == null || outPath.isBlank()) {
            ILog.e(TAG, "Output path is not set.");
            return -1;
        }

        try {
            Path out = Paths.get(outPath).toAbsolutePath().normalize();

            if (Files.exists(out) && Files.isDirectory(out)) {
                ILog.e(TAG, "Output path points to a directory. Please set outPath to a file.");
                return -3;
            }

            // Create parent directories if needed
            Path parent = out.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            // Generate the schedule
            List<String> schedule = generateSchedule(rounds, numberOfTeams, allianceSize);

            // Write to file
            Files.write(out, schedule, StandardCharsets.UTF_8);

            ILog.d(TAG, "Match schedule generated successfully with " + schedule.size() + " matches");

            return 0; // success

        } catch (IOException e) {
            ILog.e(TAG, "IO error generating match schedule: " + e.getMessage());
            return -5;
        }
    }

    /**
     * Generate a simple round-robin tournament schedule.
     */
    private List<String> generateSchedule(int rounds, int numberOfTeams, int allianceSize) {
        List<String> schedule = new ArrayList<>();

        // Add header
        schedule.add("Match Schedule");
        schedule.add("");

        // Simple round-robin: rotate teams through positions
        List<Integer> teams = new ArrayList<>();
        for (int i = 1; i <= numberOfTeams; i++) {
            teams.add(i);
        }

        int matchNumber = 1;

        for (int round = 0; round < rounds; round++) {
            // Shuffle teams for this round (but keep some determinism)
            Collections.shuffle(teams, new Random(round));

            // Create matches from the shuffled teams
            for (int i = 0; i < teams.size(); i += (allianceSize * 2)) {
                if (i + (allianceSize * 2) <= teams.size()) {
                    // Take 4 teams for a 2v2 match (allianceSize=2)
                    List<Integer> matchTeams = teams.subList(i, i + (allianceSize * 2));
                    String line = String.format("%3d: %4d %4d %4d %4d",
                            matchNumber,
                            matchTeams.get(0), matchTeams.get(1),  // red alliance
                            matchTeams.get(2), matchTeams.get(3)); // blue alliance
                    schedule.add(line);
                    matchNumber++;
                }
            }
        }

        schedule.add("");
        schedule.add("Schedule Statistics");
        schedule.add("Total Matches: " + (matchNumber - 1));

        return schedule;
    }

    private void tryEnsureExecutable(Path bin) {
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(bin);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(bin, perms);
        } catch (UnsupportedOperationException ignored) {
            // Windows or non-POSIX FS
        } catch (IOException ex) {
            ILog.w(TAG, "Unable to adjust executable permissions: " + ex.getMessage());
        }
    }

}
