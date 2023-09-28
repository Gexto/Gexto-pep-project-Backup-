package Service;

import DAO.UserLoginDAO;
import java.sql.SQLException;
import Model.Account;

public class UserLoginService {
    private final UserLoginDAO userLoginDAO;

    public UserLoginService() {
        this.userLoginDAO = new UserLoginDAO();
    }

    public Account loginUser(String username, String password) {
        try {
            //validate user credentials via DAO
            return userLoginDAO.loginUser(username, password);
        } catch (SQLException e) {
            //handle database connection / query errors
            e.printStackTrace();
            return null;
        }
    }

    public boolean userExists(int posted_by) throws SQLException {
        return userLoginDAO.userExists(String.valueOf(posted_by));
    }
}