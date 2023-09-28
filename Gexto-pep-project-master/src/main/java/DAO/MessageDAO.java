package DAO;

import Model.Message;
import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private final Connection connection;

    public MessageDAO() {
        this.connection = ConnectionUtil.getConnection();
    }

    // ===============================================================================
    public Message createMessage(Message message) throws SQLException {
        String query = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, message.getPosted_by());
            statement.setString(2, message.getMessage_text());
            statement.setLong(3, message.getTime_posted_epoch());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    int messageId = resultSet.getInt(1);
                    message.setMessage_id(messageId);
                    return message;
                }
            }
            return null; // return null if the message insertion was not successful.
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    // ===============================================================================
    public boolean deleteMessage(String messageId) throws SQLException {
        String query = "DELETE FROM message WHERE message_id = ?";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, messageId);

            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted > 0;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    // ===============================================================================
    public Message getMessageById(String messageId) throws SQLException {
        String query = "SELECT * FROM message WHERE message_id = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, messageId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int message_id = resultSet.getInt("message_id");
                int posted_by = resultSet.getInt("posted_by");
                String message_text = resultSet.getString("message_text");
                long time_posted_epoch = resultSet.getLong("time_posted_epoch");

                // create a new Message object and populate it with the retrieved data
                Message message = new Message(message_id, posted_by, message_text, time_posted_epoch);
                return message;
            } else {
                // message with the given messageId was not found
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

    // ===============================================================================
    public boolean updateMessage(String messageId, String newMessageText) throws SQLException {
        String query = "UPDATE message SET message_text = ? WHERE message_id = ?";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, newMessageText);
            statement.setString(2, messageId);

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    // ===============================================================================
    public List<Message> getAllMessages() throws SQLException {

        String query = "SELECT * FROM message";
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(query);

            resultSet = statement.executeQuery();
            List<Message> allMessages = new ArrayList<Message>();
            while (resultSet.next()) {
                int message_id = resultSet.getInt("message_id");
                int posted_by = resultSet.getInt("posted_by");
                String message_text = resultSet.getString("message_text");
                long time_posted_epoch = resultSet.getLong("time_posted_epoch");

                // create a new Message object and populate it with the retrieved data
                Message message = new Message(message_id, posted_by, message_text, time_posted_epoch);
                allMessages.add(message);
            }
            return allMessages;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    // ===============================================================================
    public List<Message> getAllMessagesForUser(int accountId) throws SQLException {

        String query = "SELECT * FROM message WHERE posted_by = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, accountId);

            resultSet = statement.executeQuery();
            List<Message> userMessages = new ArrayList<>();

            while (resultSet.next()) {
                int message_id = resultSet.getInt("message_id");
                int posted_by = resultSet.getInt("posted_by");
                String message_text = resultSet.getString("message_text");
                long time_posted_epoch = resultSet.getLong("time_posted_epoch");

                Message message = new Message(message_id, posted_by, message_text, time_posted_epoch);
                userMessages.add(message);
            }
            return userMessages;
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
