import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoFactory;
import org.thingai.scoringsystem.entity.AuthData;
import org.thingai.scoringsystem.handler.AuthHandler;

public class TestAuthHandler {
    private final AuthHandler authHandler = new AuthHandler();

    @BeforeAll
    public static void setup() {
        // Set up the DAO factory with SQLite configuration
        DaoFactory.type = "sqlite";
        DaoFactory.url = "jdbc:sqlite:src/test/resources/test.db"; // Adjust the path as needed
        Dao<AuthData, String> authDao = DaoFactory.getDao(AuthData.class); // Initialize the DAO
        authDao.facDao(new Class[] {
            AuthData.class
        }); // Ensure the DAO is ready for use
    }

    @Test
    public void testHandleAuthenticate() {
        String username = "testUser";
        String password = "testPassword";

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
}
