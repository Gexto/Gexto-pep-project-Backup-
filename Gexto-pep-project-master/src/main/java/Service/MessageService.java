package Service;

import java.sql.SQLException;
import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private final MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    // ===============================================================================
    public Message createMessage(Message message) {
        try {
            // call the DAO method to create a new message
            return messageDAO.createMessage(message);
        } catch (SQLException e) {
            // handle any database-related exceptions here
            e.printStackTrace();
            return null; // return null or throw a custom exception
        }
    }

    // ===============================================================================
    public Message deleteMessage(String messageId) {
        try {
            // check if the message with the given messageId exists
            Message existingMessage = messageDAO.getMessageById(messageId);
            if (existingMessage == null) {
                // message not found, return null or throw an exception
                return null; // or throw a custom exception
            }

            // call the DAO method to delete the message by its messageId
            boolean isDeleted = messageDAO.deleteMessage(messageId);
            return isDeleted ? existingMessage : null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null; // or throw a custom exception
        }
    }

    // ===============================================================================
    public Message updateMessage(String messageId, String newMessageText) {
        try {
            // Check if the message with the given messageId exists
            Message existingMessage = messageDAO.getMessageById(messageId);
            if (existingMessage == null) {
                // message not found, return null or throw an exception
                return null; // or throw a custom exception
            }

            // Call the DAO method to update the message text
            boolean isUpdated = messageDAO.updateMessage(messageId, newMessageText);

            if (isUpdated) {
                // message updated successfully
                existingMessage.setMessage_text(newMessageText); // update the existing message object
                return existingMessage;
            } else {
                // Update failed
                return null; // throw an exception
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // throw a custom exception
        }
    }

    public Message getMessageById(String messageId) throws SQLException {
        return messageDAO.getMessageById(messageId);
    }

    // ===============================================================================
    public List<Message> allMessages() throws SQLException {
        return messageDAO.getAllMessages();
    }

    // ===============================================================================
    public List<Message> getAllMessagesForUser(int accountId) throws SQLException {
        return messageDAO.getAllMessagesForUser(accountId);

    }
}
