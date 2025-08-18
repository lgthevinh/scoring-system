package org.thingai.scoringsystem.handler;

import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFactory;
import org.thingai.scoringsystem.entity.AuthData;

import java.security.SecureRandom;

public class AuthHandler {
    private static String SECRET_KEY = "secret_key";
    private Dao<AuthData, String> authDataDao = DaoFactory.getDao();

    public interface AuthHandlerCallback {
        void onSuccess();
        void onFailure();
    }

    public static void handleAuthenticate(String username, String password, AuthHandlerCallback callback) {

    }

    public static void handleCreateAuth(String username, String password, AuthHandlerCallback callback) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
    }

}
