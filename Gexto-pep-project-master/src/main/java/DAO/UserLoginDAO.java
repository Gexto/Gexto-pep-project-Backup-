package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Model.Account;
import Util.ConnectionUtil;

public class UserLoginDAO {
    private final Connection connection;

    public UserLoginDAO() {
        this.connection = ConnectionUtil.getConnection();
    }

    public Account loginUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM Account WHERE username = ? AND password = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                String retrievedUsername = resultSet.getString("username");
                
                Account userAccount = new Account(accountId, retrievedUsername, password);
                
                return userAccount;
            } else {
                return null;
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    public boolean userExists(String posted_by)throws SQLException {
        String query = "SELECT * FROM Account WHERE account_id = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
    
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, posted_by);
    
            resultSet = statement.executeQuery();
    
            return resultSet.next(); //if a row is found, the user exists; otherwise no.
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
}