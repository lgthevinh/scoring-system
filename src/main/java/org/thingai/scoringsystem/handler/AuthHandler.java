package org.thingai.scoringsystem.handler;

import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFactory;
import org.thingai.scoringsystem.entity.AuthData;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import static org.thingai.scoringsystem.util.ByteUtil.bytesToHex;
import static org.thingai.scoringsystem.util.ByteUtil.hexToBytes;

public class AuthHandler {
    private static final String SECRET_KEY = "secret_key";
    private static final int TOKEN_EXPIRATION_TIME = 3600 * 1000; // 1 hour in milliseconds

    private Dao<AuthData, String> authDataDao;

    public interface AuthHandlerCallback {
        void onSuccess(String token, String successMessage);
        void onFailure(String errorMessage);
    }

    public void handleAuthenticate(String username, String password, AuthHandlerCallback callback) {
        if (authDataDao == null) {
            authDataDao = DaoFactory.getDao(AuthData.class);
        }

        try {
            List<AuthData> authDataList = authDataDao.query(new String[]{"username"}, new String[]{username});

            if (authDataList.isEmpty()) {
                callback.onFailure("Authentication failed: User not found.");
                return;
            }

            AuthData authData = authDataList.get(0);

            // Hash the provided password with the stored salt
            byte[] salt = hexToBytes(authData.getSalt());
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // Compare the hashed password with the stored password
            if (authData.getPassword().equals(bytesToHex(hashedPassword))) {
                String token = generateToken(username);
                callback.onSuccess(token, "Authentication successful.");
            } else {
                callback.onFailure("Authentication failed: Incorrect password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure("Authentication failed: " + e.getMessage());
        }
    }

    public void handleCreateAuth(String username, String password, AuthHandlerCallback callback) {
        // Hash password with salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        // Create AuthData object
        AuthData authData = new AuthData();
        authData.setUsername(username);
        authData.setPassword(bytesToHex(hashedPassword));
        authData.setSalt(bytesToHex(salt));

        // Save AuthData to database
        if (authDataDao == null) {
            authDataDao = DaoFactory.getDao(AuthData.class);
        }
        try {
            authDataDao.insert(authData);
            String token = generateToken(username);
            callback.onSuccess(token, "Authentication created successfully.");
        } catch (Exception e) {
            callback.onFailure("Failed to create authentication: " + e.getMessage());
        }
    }

    private String generateToken(String username) {
        long timestamp = System.currentTimeMillis();
        String tokenData = username + ":" + timestamp + ":" + SECRET_KEY;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] tokenBytes = md.digest(tokenData.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(tokenBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating token: " + e.getMessage());
        }
    }

    private boolean validateToken(String token) {
        try {
            String[] parts = token.split(":");
            if (parts.length != 3) {
                return false;
            }
            // String username = parts[0]; // Not used in validation, but could be used for logging or further checks
            long timestamp = Long.parseLong(parts[1]);
            String secretKey = parts[2];

            // Check if the token is expired
            if (System.currentTimeMillis() - timestamp > TOKEN_EXPIRATION_TIME) {
                return false;
            }

            // Validate the secret key
            return SECRET_KEY.equals(secretKey);
        } catch (Exception e) {
            return false;
        }
    }

}
