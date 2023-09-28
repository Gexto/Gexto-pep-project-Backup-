package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Model.Message;
import Service.MessageService;
import Service.UserLoginService;
import Service.UserRegistrationService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SocialMediaController {
    private final UserLoginService userLoginService;
    private final UserRegistrationService userRegistrationService;
    private final MessageService messageService;

    public SocialMediaController() {
        this.userLoginService = new UserLoginService();
        this.userRegistrationService = new UserRegistrationService();
        this.messageService = new MessageService();
    }

    public SocialMediaController(UserLoginService userLoginService, UserRegistrationService userRegistrationService,
            MessageService messageService) {
        this.userLoginService = userLoginService;
        this.userRegistrationService = userRegistrationService;
        this.messageService = messageService;
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // defined endpoints and handlers
        app.post("/register", this::registerUser);
        app.post("/login", this::loginUser);
        app.post("/messages", this::createMessage);
        app.delete("messages/{messageId}", this::deleteMessage);
        app.patch("messages/{messageId}", this::updateMessage);
        app.get("messages/{messageId}", this::retrieveMessageId);
        app.get("messages", this::retrieveAllMessages);
        app.get("accounts/{accountId}/messages", this::retriveAllMessagesForUser);

        return app;
    }

    // ===============================================================================
    private void registerUser(Context context) {
        // extract username and password from the request body
        ObjectMapper mapper = new ObjectMapper();

        try {
            Account newAccount = mapper.readValue(context.body(), Account.class);
            if (newAccount != null &&
                    !newAccount.getUsername().equals("") &&
                    !newAccount.getPassword().equals("") &&
                    newAccount.getPassword().length() > 4) {
                Account registeredUser = userRegistrationService.registerUser(newAccount.getUsername(),
                        newAccount.getPassword());
                if (registeredUser != null) {
                    context.json(registeredUser); // return the registered user as JSON
                } else {
                    context.status(400).json(""); // registration failed
                }
            } else {
                context.status(400).json(""); // registration failed
            }
        } catch (Exception e) {
            context.status(500).json("Internal Server Error"); // handle exceptions
        }
    }

    // ===============================================================================
    private void loginUser(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Account loginAccount = mapper.readValue(context.body(), Account.class);
            if (loginAccount != null &&
                    !loginAccount.getUsername().isEmpty() &&
                    !loginAccount.getPassword().isEmpty()) {

                Account authenticatedUser = userLoginService.loginUser(loginAccount.getUsername(),
                        loginAccount.getPassword());

                if (authenticatedUser != null) {
                    loginSuccessful(context, authenticatedUser); // successful login
                } else {
                    loginInvalidUserName(context); // invalid username
                }
            } else {
                loginInvalidPassword(context); // invalid password
            }
        } catch (Exception e) {
            context.status(500).json("Internal Server Error"); // handle exceptions
        }
    }

    // -------------------------------------------------------------------------------
    private void loginSuccessful(Context context, Account authenticatedUser) {
        context.json(authenticatedUser); // successful login
    }

    private void loginInvalidUserName(Context context) {
        context.status(401).json(""); // invalid username
    }

    private void loginInvalidPassword(Context context) {
        context.status(401).json(""); // invalid password
    }

    // ===============================================================================
    private void createMessage(Context context) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Message newMessage = mapper.readValue(context.body(), Message.class);
            // checks if the deserialization was successful
            if (newMessage != null) {
                boolean userExists = userLoginService.userExists(newMessage.getPosted_by());

                // checks additional criteria for message creation
                if (!newMessage.getMessage_text().isEmpty() &&
                        newMessage.getMessage_text().length() <= 254 && userExists) {

                    // create the message
                    Message createdMessage = messageService.createMessage(newMessage);

                    if (createdMessage != null) {
                        createMessageSuccessful(context, createdMessage); // return the created message as JSON
                    } else {
                        createMessageFailed(context); // message creation failed
                    }
                } else {
                    createMessageInvalid(context); // message creation failed due to invalid criteria
                }
            }
        } catch (Exception e) {
            context.status(500).json("Something else Error"); // handle exceptions
        }
    }

    // -------------------------------------------------------------------------------
    private void createMessageSuccessful(Context context, Message createdMessage) {
        context.json(createdMessage); // successful message creation
    }

    private void createMessageFailed(Context context) {
        context.status(200).json(""); // message creation failed
    }

    private void createMessageInvalid(Context context) {
        context.status(400).json(""); // message creation failed due to invalid criteria
    }

    // ===============================================================================
    private void deleteMessage(Context context) {
        String messageId = context.pathParamMap().get("messageId");
        Message message = messageService.deleteMessage(messageId);

        if (message != null)
            context.status(200).json(message);
        else
            context.status(200).json("");
    }

    // ===============================================================================
    private void updateMessageSuccessful(Context context, Message updatedMessage) {
        context.status(200).json(updatedMessage); // successful update with the updated message as JSON
    }

    private void updateMessageNotFound(Context context) {
        context.status(400).json(""); // message not found
    }

    private void messageNotFound(Context context) {
        context.status(200).json("");
    }

    private void updateMessageStringEmpty(Context context) {
        context.status(400).json(""); // message text is empty
    }

    private void messageFound(Context context, Message message) {
        context.status(200).json(message);
    }

    private void allMessagesFound(Context context, List<Message> allMessages) {
        context.status(200).json(allMessages);
    }

    private void allMessagesNotFound(Context context) {
        context.status(200).json(new ArrayList<Message>());
    }

    private void updateMessageTooLong(Context context) {
        context.status(400).json(""); // message text is too long
    }

    // ---------------------------------------------------------------------------------
    private void updateMessage(Context context) throws SQLException, JsonMappingException, JsonProcessingException {
        String messageId = context.pathParam("messageId");

        // retrieve the new message text from the request body
        ObjectMapper mapper = new ObjectMapper();
        Message newMessage = mapper.readValue(context.body(), Message.class);
        String newMessageText = newMessage.getMessage_text();
        if (newMessageText.isEmpty()) {
            updateMessageStringEmpty(context);
        } else if (newMessageText.length() > 254) {
            updateMessageTooLong(context);
        } else {
            Message updatedMessage = messageService.updateMessage(messageId, newMessageText);
            if (messageService.getMessageById(messageId) == null) {
                updateMessageNotFound(context);
            } else if (updatedMessage != null) {
                updateMessageSuccessful(context, updatedMessage);
            }
        }
    }

    // ===============================================================================
    private void retrieveMessageId(Context context) throws SQLException {

        String messageId = context.pathParam("messageId");

        Message message = messageService.getMessageById(messageId);

        if (message == null) {
            messageNotFound(context);
        } else {
            messageFound(context, message);
        }
    }

    // ===============================================================================
    private void retrieveAllMessages(Context context) throws SQLException {
        List<Message> allMessages = messageService.allMessages();

        if (allMessages != null && !allMessages.isEmpty()) {
            allMessagesFound(context, allMessages);
        } else {
            allMessagesNotFound(context);
        }

    }

    // ===============================================================================
    private void allMessagesFromUserMessageExists(Context context, List<Message> getAllMessagesForUser) {
        context.status(200).json(getAllMessagesForUser);
    }

    private void allMessagesFromUserNoMessagesFound(Context context) {
        context.status(200).json(new ArrayList<Message>());
    }

    private void retriveAllMessagesForUser(Context context) throws SQLException {
        String accountIdParam = context.pathParam("accountId");

        int accountId = Integer.parseInt(accountIdParam);
        List<Message> allMessagesForUser = messageService.getAllMessagesForUser(accountId);

        if (allMessagesForUser.isEmpty()) {
            allMessagesFromUserNoMessagesFound(context);
        } else {
            allMessagesFromUserMessageExists(context, allMessagesForUser);
        }
    }
}
