package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Model.Account;
import Util.ConnectionUtil;

public class UserRegistrationDAO {
    private final Connection connection;

    public UserRegistrationDAO() {
        this.connection = ConnectionUtil.getConnection();
    }

    public Account registerUser(String username, String password) throws SQLException {
        //check if the username already exists in the database
        if (isUsernameTaken(username)) {
            return null; //username is already takenn registration failed
        }

        //insert new user account into the database
        String insertQuery = "INSERT INTO Account (username, password) VALUES (?, ?)";
        PreparedStatement statement = null;
        
        try {
            statement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2, password);
            
            //execute the INSERT statement
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 1) {
                //registration was sucessful
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int accountId = generatedKeys.getInt(1);
                    return new Account(accountId, username, password);
                }
            }
        } finally {
            //close the PreparedStatement manually
            if (statement != null) {
                statement.close();
            }
        }

        return null; //registration failed
    }

    private boolean isUsernameTaken(String username) throws SQLException {
        String query = "SELECT account_id FROM Account WHERE username = ?";
        PreparedStatement statement = null;
        
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            
            ResultSet resultSet = statement.executeQuery();
            
            return resultSet.next(); //returns true if username exists
        } finally {
            //close the PreparedStatement manually
            if (statement != null) {
                statement.close();
            }
        }
    }
}
