package Server.notifications;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Domain.Entities.User;
import Domain.Entities.Alerts.Alert;
import Domain.Repositories.DbUserRepository;
import Domain.Repositories.InterfaceUserRepository;
import Exceptions.StockMarketException;
import Exceptions.UserException;

/**
 * Handles notifications by sending messages through a WebSocket server.
 */
@Service
public class NotificationHandler {

    private WebSocketServer wServer;
    private InterfaceUserRepository _userRepository;

    @Autowired
    private NotificationHandler(WebSocketServer wServer, DbUserRepository dbUserRepository) {
        this.wServer = wServer;
        this._userRepository = dbUserRepository;
    }

    // set the repositories to be used test time
    public void setNotificationFacadeRepositories(InterfaceUserRepository userRepository) {
        this._userRepository = userRepository;
    }

    public NotificationHandler() {
    }

    /**
     * 
     * /**
     * Sends an alert message to a specified user via the WebSocket server.
     * Converts the Alert object to a string message before sending.
     *
     * @param targetUsername The username of the recipient.
     * @param alert          The Alert object containing the message to be sent.
     * @throws StockMarketException
     */
    @Transactional
    public void sendMessage(String targetUsername, Alert alert) throws StockMarketException {
        String message = alert.getMessage();
        wServer.sendMessage(targetUsername, message);

        User user = getUserByUsername(targetUsername);
        user.addMessage(message);
        _userRepository.flush();
    }

    /**
     * Retrieves previous messages for a specific user.
     *
     * @param targetUsername the username of the target user
     */
    public void retrivePreviousMessages(String targetUsername) {

        wServer.retrivePreviousMessages(targetUsername);
    }

    /**
     * Gets a user by username.
     *
     * @param username the username of the user
     * @return the User object
     * @throws StockMarketException if the user does not exist or the username is
     *                              null
     */
    public User getUserByUsername(String username) throws StockMarketException {
        if (username == null)
            throw new UserException("Username is null.");
        if (!doesUserExist(username))
            throw new UserException(String.format("Username %s does not exist.", username));
        return _userRepository.findByusername(username);
    }

    /**
     * Checks if a user exists in the system.
     *
     * @param username the username of the user
     * @return true if the user exists, false otherwise
     */
    public boolean doesUserExist(String username) {
        return _userRepository.existsByusername(username);
    }

    /**
     * Sends a message to all users.
     *
     * @param alert the Alert object containing the message to be sent
     * @throws StockMarketException if an error occurs while sending the message
     */
    @Transactional
    public void sendMessageToAllUsers(Alert alert) throws StockMarketException {
        String message = alert.getMessage();
        for (User user : getAllUsers()) {
            wServer.sendMessage(user.getUserName(), message);
            user.addMessage(message);
        }
        _userRepository.flush();
    }

     /**
     * Retrieves all users in the system.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return _userRepository.findAll();
    }
      /**
     * Deletes all messages for a specific user.
     *
     * @param username the username of the user
     * @throws StockMarketException if an error occurs while deleting the messages
     */
    @Transactional
    public void deleteUserMessages(String username) throws StockMarketException {
        User user = getUserByUsername(username);
        // user.clearMessages();
        _userRepository.flush();
    }

      /**
     * Sends a message to a group of users.
     *
     * @param usernames the list of usernames to send the message to
     * @param alert     the Alert object containing the message to be sent
     * @throws StockMarketException if an error occurs while sending the message
     */
    @Transactional
    public void sendMessageToGroup(List<String> usernames, Alert alert) throws StockMarketException {
        String message = alert.getMessage();
        for (String username : usernames) {
            wServer.sendMessage(username, message);
            User user = getUserByUsername(username);
            user.addMessage(message);
        }
        _userRepository.flush();
    }

    /**
     * Retrieves all messages for a specific user.
     *
     * @param username the username of the user
     * @return a list of messages for the user
     * @throws StockMarketException if an error occurs while retrieving the messages
     */
    public List<String> retrieveMessagesForUser(String username) throws StockMarketException {
        User user = getUserByUsername(username);
        return user.getMessages();
    }

    /**
     * Marks all messages as read for a specific user.
     *
     * @param username the username of the user
     * @throws StockMarketException if an error occurs while marking the messages as read
     */
    @Transactional
    public void markMessagesAsRead(String username) throws StockMarketException {
        User user = getUserByUsername(username);
        // user.markMessagesAsRead();
        _userRepository.flush();
    }

    /**
     * Gets the count of unread messages for a specific user.
     *
     * @param username the username of the user
     * @return the count of unread messages
     * @throws StockMarketException if an error occurs while retrieving the count
     */
    // public int getUnreadMessagesCount(String username) throws StockMarketException {
    //     User user = getUserByUsername(username);
    //     return user.getUnreadMessagesCount();
    // }


    //   /**
    //  * Filters messages for a specific user based on criteria.
    //  *
    //  * @param username the username of the user
    //  * @param criteria the criteria to filter messages
    //  * @return a list of filtered messages
    //  * @throws StockMarketException if an error occurs while filtering the messages
    //  */
    // public List<String> filterMessagesForUser(String username, String criteria) throws StockMarketException {
    //     User user = getUserByUsername(username);
    //     return user.filterMessages(criteria);
    // }

    

}
