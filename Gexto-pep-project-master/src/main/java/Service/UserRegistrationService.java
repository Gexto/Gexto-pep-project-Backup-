package Service;

import java.sql.SQLException;
import Model.Account;
import DAO.UserRegistrationDAO;

public class UserRegistrationService {
    private final UserRegistrationDAO userRegistrationDAO;

    public UserRegistrationService() {
        this.userRegistrationDAO = new UserRegistrationDAO();
    }

    public Account registerUser(String username, String password) {
        try {
            //validate user registration
            return userRegistrationDAO.registerUser(username, password);
        } catch (SQLException e) {
            //handle database connection or query errors
            e.printStackTrace();
            return null;
        }
    }
}
