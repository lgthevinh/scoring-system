import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.scoringsystem.entity.AuthData;
import org.thingai.scoringsystem.handler.AuthHandler;

public class TestAuthHandler {
    private final AuthHandler authHandler = new AuthHandler();

    @BeforeAll
    public static void setup() {
        // Set up the DAO factory with SQLite configuration
        String url = "jdbc:sqlite:src/test/resources/test.db"; // Adjust the path as needed
        Dao<AuthData, String> authDao = new DaoSqlite<>(AuthData.class, url);
        authDao.initDao(new Class[] {
            AuthData.class
        }); // Ensure the DAO is ready for use
    }

    @Test
    public void testHandleCreateAuth() {
        String username = "newUser";
        String password = "newPassword";

        new AuthHandler().handleCreateAuth(username, password, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String token, String successMessage) {
                System.out.println("User created successfully: " + successMessage);
                System.out.println("Token: " + token);
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println("User creation failed: " + errorMessage);
            }
        });
    }

    @Test
    public void testHandleAuthenticate() {
        String username = "newUser";
        String password = "newPassword";

        new AuthHandler().handleAuthenticate(username, password, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String token, String successMessage) {
                System.out.println("Authentication successful: " + successMessage);
                System.out.println("Token: " + token);
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println(errorMessage);
            }
        });
    }

    @Test
    public void testHandleAuthenticateWithWrongPassword() {
        String username = "newUser";
        String password = "wrongPassword";

        new AuthHandler().handleAuthenticate(username, password, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String token, String successMessage) {
                System.out.println("Authentication should not succeed with wrong password.");
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println("Expected failure: " + errorMessage);
            }
        });
    }

    @Test
    public void testHandleValidateToken() {
        String username = "newUser";
        String password = "newPassword";

        AuthHandler authHandler = new AuthHandler();
        final String[] newToken = new String[1];

        authHandler.handleAuthenticate(username, password, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String token, String successMessage) {
                System.out.println(successMessage);
                System.out.println("Token: " + token);
                newToken[0] = token; // Store the token for validation
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println(errorMessage);
            }
        });

        // Now validate the token
        authHandler.handleValidateToken(newToken[0], new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String validToken, String successMessage) {
                System.out.println("Token validation successful: " + successMessage);
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println("Token validation failed: " + errorMessage);
            }
        });
    }

    @Test
    public void testHandleValidateTokenWithInvalidToken() {
        String invalidToken = "invalid:token";
        new AuthHandler().handleValidateToken(invalidToken, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String validToken, String successMessage) {
                System.out.println("This should not succeed with an invalid token.");
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println("Expected failure: " + errorMessage);
            }
        });
    }

    @Test
    public void testRefreshToken() {
        String username = "newUser";
        String password = "newPassword";

        AuthHandler authHandler = new AuthHandler();
        final String[] newToken = new String[1];

        authHandler.handleAuthenticate(username, password, new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String token, String successMessage) {
                System.out.println(successMessage);
                System.out.println("Token: " + token);
                newToken[0] = token; // Store the token for refreshing
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println(errorMessage);
            }
        });

        // Now refresh the token
        authHandler.handleRefreshToken(newToken[0], new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String refreshedToken, String successMessage) {
                System.out.println("Token refresh successful: " + successMessage);
                System.out.println("Refreshed Token: " + refreshedToken);
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println("Token refresh failed: " + errorMessage);
            }
        });

        // Validate the refreshed token
        authHandler.handleValidateToken(newToken[0], new AuthHandler.AuthHandlerCallback() {
            @Override
            public void onSuccess(String validToken, String successMessage) {
                System.out.println("Refreshed token validation successful: " + successMessage);
            }

            @Override
            public void onFailure(String errorMessage) {
                System.err.println("Refreshed token validation failed: " + errorMessage);
            }
        });
    }

}
