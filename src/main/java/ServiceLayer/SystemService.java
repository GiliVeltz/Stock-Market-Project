package ServiceLayer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Domain.Alerts.Alert;
import Domain.Alerts.GeneralAlert;
import Domain.ExternalServices.ExternalServiceHandler;
import Domain.Facades.ShoppingCartFacade;
import Domain.Facades.UserFacade;
import Dtos.ExternalServiceDto;

// Class that represents the system service and enables users (probably admins) to control the system.

@SuppressWarnings({"rawtypes" , "unchecked"})
@Service
public class SystemService {
    private ExternalServiceHandler _externalServiceHandler;
    private boolean _isOpen = false;
    private TokenService _tokenService;
    private UserFacade _userFacade;
    private ShoppingCartFacade _shoppingCartFacade;
    private static final Logger logger = Logger.getLogger(SystemService.class.getName());

    public SystemService(ExternalServiceHandler externalServiceHandler,
            TokenService tokenService, UserFacade userFacade, ShoppingCartFacade shoppingCartFacade) {
        _externalServiceHandler = externalServiceHandler;
        _tokenService = tokenService;
        _userFacade = userFacade;
        _shoppingCartFacade = shoppingCartFacade;
        // TODO: create it as a singleton
        _externalServiceHandler = externalServiceHandler;
    }

    public SystemService() {
        _externalServiceHandler = new ExternalServiceHandler();
        _tokenService = TokenService.getTokenService();
        _userFacade = UserFacade.getUserFacade();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
    }

    /**
     * Opens the system.
     * 
     * @param userId   the user ID
     * @param password the user password
     * @return a response indicating the success or failure of opening the system
     */
    @Transactional
    public ResponseEntity<Response> openSystem(String token) {
        Response response = new Response();
        try {
            String username = _tokenService.extractUsername(token);
            if (_tokenService.validateToken(token)) {
                // Check if the user is already logged in.
                if (!_tokenService.isUserAndLoggedIn(token)) {
                    response.setErrorMessage("User is not logged in");
                    logger.log(Level.SEVERE, "User is not logged in");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
                // Check if the user is an admin
                if(!_userFacade.isAdmin(username)){
                    response.setErrorMessage("User is not an admin");
                    logger.log(Level.SEVERE, "User is not an admin");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }

                // Check if the system is already open
                if (isSystemOpen()) {
                    response.setErrorMessage("System is already open");
                    logger.log(Level.SEVERE, "System is already open");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }

                // Connect to external services
                if (!_externalServiceHandler.connectToServices()) {
                    response.setErrorMessage("Failed to connect to external services");
                    logger.log(Level.SEVERE, "Failed to connect to external services");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }

                // Open the system
                setSystemOpen(true);
                _externalServiceHandler.addPaymentService("PaymentService", "Tal", "123456789");
                _externalServiceHandler.addSupplyService("SupplyService", "Tal", "123456789");
                logger.info("System opened by admin: " + username);
                response.setReturnValue("System Opened Successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to open system: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to open system: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Set system to open
    public void setSystemOpen(boolean isOpen) {
        this._isOpen = isOpen;
    }

    // Check if system is open
    public boolean isSystemOpen() {
        return _isOpen;
    }

    /**
     * This is the first request that will occur when a new guest enters the system.
     * It generates a guest token and initializes a new cart with the guestID.
     *
     * @return Response object containing the generated guest token if successful,
     *         or an error message if there is a failure.
     */
    @Transactional
    public ResponseEntity<Response> requestToEnterSystem() {
        Response response = new Response();
        try {
            String token = _tokenService.generateGuestToken();
            String id = _tokenService.extractGuestId(token);
            logger.info("New guest entered into the system, ID:" + id);
            _userFacade.addNewGuest(id);
            _shoppingCartFacade.addCartForGuest(id);
            response.setReturnValue(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setErrorMessage("Guest uuid failed: " + e.getMessage());
            logger.log(Level.SEVERE, "Guest uuid failed: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This is the last request that will occur when a guest/user leave the system.
     * It removes the guest from the user system, and removes the guest's shopping
     * cart.
     *
     * @param token The session token for the guest.
     * @return Response object indicating the success or failure of the operation.
     */
    @Transactional
    public ResponseEntity<Response> leaveSystem(String token) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String id = _tokenService.extractGuestId(token);
                if (id != null) {
                    logger.info("Guest with id: " + id + " left the system");
                    _userFacade.removeGuest(id);
                    _shoppingCartFacade.removeCartForGuest(id);
                    response.setReturnValue("Guest left system Successfully");
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to leave the system: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to leave the system: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     // add external service to the system
    @Transactional
     public ResponseEntity<Response> addExternalService(String token, ExternalServiceDto externalServiceDto) {
        Response response = new Response();
        try {
            // check validation of token
            if (!_tokenService.validateToken(token)) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            String username = _tokenService.extractUsername(token);
            // check if user is logged in
            if (!_tokenService.isUserAndLoggedIn(token)) {
                response.setErrorMessage("User is not logged in");
                logger.log(Level.SEVERE, "User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if user is admin
            if (!_userFacade.isAdmin(username)) {
                response.setErrorMessage("User is not admin of the system");
                logger.log(Level.SEVERE, "User is not admin of the system");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if system is open
            if (!isSystemOpen()) {
                response.setErrorMessage("System is not open");
                logger.log(Level.SEVERE, "System is not open");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check validation of the arguments
            if(externalServiceDto.getServiceName() == null || externalServiceDto.getServiceName().length() == 0 || externalServiceDto.getInformationPersonName() == null
             || externalServiceDto.getInformationPersonName().length() == 0 || externalServiceDto.getInformationPersonPhone() == null || externalServiceDto.getInformationPersonPhone().length() == 0) {
                response.setErrorMessage("One or more of the arguments are null");
                logger.log(Level.SEVERE, "One or more of the arguments are null");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (_externalServiceHandler.addExternalService(externalServiceDto)) {
                logger.info("External service: " + externalServiceDto.getServiceName() + " added by admin: " + username);
                response.setReturnValue("External service added successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setErrorMessage("Failed to add external service");
                logger.log(Level.SEVERE, "Failed to add external service");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to add external service: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to add external service: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // change external service name
    @Transactional
    public ResponseEntity<Response> changeExternalServiceName(String token, ExternalServiceDto externalServiceDto, String newServiceName) {
        Response response = new Response();
        try {
            // check validation of token
            if (!_tokenService.validateToken(token)) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            String username = _tokenService.extractUsername(token);
            // check if user is logged in
            if (!_tokenService.isUserAndLoggedIn(token)) {
                response.setErrorMessage("User is not logged in");
                logger.log(Level.SEVERE, "User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if user is admin
            if (!_userFacade.isAdmin(username)) {
                response.setErrorMessage("User is not admin of the system");
                logger.log(Level.SEVERE, "User is not admin of the system");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if system is open
            if (!isSystemOpen()) {
                response.setErrorMessage("System is not open");
                logger.log(Level.SEVERE, "System is not open");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check validation of the arguments
            if(newServiceName == null || newServiceName.length() == 0){
                response.setErrorMessage("One or more of the arguments are null");
                logger.log(Level.SEVERE, "One or more of the arguments are null");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if service id and name exists
            if (!(_externalServiceHandler.isServiceExistsByName(externalServiceDto.getServiceName()) && _externalServiceHandler.isServiceExistsById(externalServiceDto.getId()))) {
                response.setErrorMessage("Service id not found. Service name: " + externalServiceDto.getServiceName() + ".");
                logger.log(Level.SEVERE, "Service id not found. Service name: " + externalServiceDto.getServiceName() + ".");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (_externalServiceHandler.changeExternalServiceName(externalServiceDto.getId(), newServiceName)) {
                logger.info("External service: " + externalServiceDto.getServiceName() + " name changed to: " + newServiceName + " by admin: " + username);
                response.setReturnValue("External service name changed successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setErrorMessage("Failed to change external service name");
                logger.log(Level.SEVERE, "Failed to change external service name");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to change external service name: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to change external service name: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // change external service information person name
    @Transactional
    public ResponseEntity<Response> changeExternalServiceInformationPersonName(String token, ExternalServiceDto externalServiceDto, String newServiceInformationPersonName){
        Response response = new Response();
        try {
            // check validation of token
            if (!_tokenService.validateToken(token)) {
                throw new Exception("Invalid session token.");
            }
            String username = _tokenService.extractUsername(token);
            // check if user is logged in
            if (!_tokenService.isUserAndLoggedIn(token)) {
                response.setErrorMessage("User is not logged in");
                logger.log(Level.SEVERE, "User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            // check if user is admin
            if (!_userFacade.isAdmin(username)) {
                response.setErrorMessage("User is not admin of the system");
                logger.log(Level.SEVERE, "User is not admin of the system");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if system is open
            if (!isSystemOpen()) {
                response.setErrorMessage("System is not open");
                logger.log(Level.SEVERE, "System is not open");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check validation of the arguments
            if(newServiceInformationPersonName == null || newServiceInformationPersonName.length() == 0){
                response.setErrorMessage("One or more of the arguments are null");
                logger.log(Level.SEVERE, "One or more of the arguments are null");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if service id and name exists
            if (!(_externalServiceHandler.isServiceExistsByName(externalServiceDto.getServiceName()) && _externalServiceHandler.isServiceExistsById(externalServiceDto.getId()))) {
                response.setErrorMessage("Service id not found. Service name: " + externalServiceDto.getServiceName() + ".");
                logger.log(Level.SEVERE, "Service id not found. Service name: " + externalServiceDto.getServiceName() + ".");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (_externalServiceHandler.changeExternalServiceInformationPersonName(externalServiceDto.getId(), newServiceInformationPersonName)) {
                logger.info("External service: " + externalServiceDto.getServiceName() + " information person name changed to: " + newServiceInformationPersonName + " by admin: " + username);
                response.setReturnValue("External service information person name changed successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setErrorMessage("Failed to change external service information person name");
                logger.log(Level.SEVERE, "Failed to change external service information person name");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to change external service information person name: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to change external service information person name: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // change external service information person phone
    @Transactional
    public ResponseEntity<Response> changeExternalServiceInformationPersonPhone(String token, ExternalServiceDto externalServiceDto, String newServiceInformationPersonPhone){
        Response response = new Response();
        try {
            // check validation of token
            if (!_tokenService.validateToken(token)) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            String username = _tokenService.extractUsername(token);
            // check if user is logged in
            if (!_tokenService.isUserAndLoggedIn(token)) {
                response.setErrorMessage("User is not logged in");
                logger.log(Level.SEVERE, "User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if user is admin
            if (!_userFacade.isAdmin(username)) {
                response.setErrorMessage("User is not admin of the system");
                logger.log(Level.SEVERE, "User is not admin of the system");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if system is open
            if (!isSystemOpen()) {
                response.setErrorMessage("System is not open");
                logger.log(Level.SEVERE, "System is not open");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check validation of the arguments
            if(newServiceInformationPersonPhone == null || newServiceInformationPersonPhone.length() == 0){
                response.setErrorMessage("One or more of the arguments are null");
                logger.log(Level.SEVERE, "One or more of the arguments are null");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if service id and name exists
            if (!(_externalServiceHandler.isServiceExistsByName(externalServiceDto.getServiceName()) && _externalServiceHandler.isServiceExistsById(externalServiceDto.getId()))) {
                response.setErrorMessage("Service id not found. Service name: " + externalServiceDto.getServiceName() + ".");
                logger.log(Level.SEVERE, "Service id not found. Service name: " + externalServiceDto.getServiceName() + ".");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (_externalServiceHandler.changeExternalServiceInformationPersonPhone(externalServiceDto.getId(), newServiceInformationPersonPhone)) {
                logger.info("External service: " + externalServiceDto.getServiceName() + " information person phone changed to: " + newServiceInformationPersonPhone + " by admin: " + username);
                response.setReturnValue("External service information person phone changed successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setErrorMessage("Failed to change external service information person phone");
                logger.log(Level.SEVERE, "Failed to change external service information person phone");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to change external service information person phone: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to change external service information person phone: " + e.getMessage(), e);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // send alert notification to targetUser with message
    @Transactional
    public ResponseEntity<Response> sendAlertNotification(String token, String targetUser, String message) {
        Response response = new Response();
        try {
            // check validation of token
            if (!_tokenService.validateToken(token)) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            // check if user is logged in
            if (!_tokenService.isUserAndLoggedIn(token)) {
                response.setErrorMessage("User is not logged in");
                logger.log(Level.SEVERE, "User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check if system is open
            if (!isSystemOpen()) {
                response.setErrorMessage("System is not open");
                logger.log(Level.SEVERE, "System is not open");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            // check validation of the arguments
            if(targetUser == null || targetUser.length() == 0 || message == null || message.length() == 0){
                response.setErrorMessage("One or more of the arguments are null");
                logger.log(Level.SEVERE, "One or more of the arguments are null");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            String fromUser = _tokenService.extractUsername(token);
            Alert generalAlert = new GeneralAlert(fromUser,targetUser, message);
            if(_userFacade.notifyUser(targetUser, generalAlert)){
                logger.info("Alert notification sent to user: " + targetUser + " by admin: " + fromUser);
                response.setReturnValue("Alert notification sent successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
             else {
                response.setErrorMessage("Failed to send alert notification");
                logger.log(Level.SEVERE, "Failed to send alert notification");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to send alert notification: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to send alert notification: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}