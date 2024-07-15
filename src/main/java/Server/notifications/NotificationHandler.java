package Server.notifications;

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

    public void retrivePreviousMessages(String targetUsername) {

        wServer.retrivePreviousMessages(targetUsername);
    }

    // function to get a user by username
    public User getUserByUsername(String username) throws StockMarketException {
        if (username == null)
            throw new UserException("Username is null.");
        if (!doesUserExist(username))
            throw new UserException(String.format("Username %s does not exist.", username));
        return _userRepository.findByusername(username);
    }

    // function to check if a user exists in the system
    public boolean doesUserExist(String username) {
        return _userRepository.existsByusername(username);
    }

}
