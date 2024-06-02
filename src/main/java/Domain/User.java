package Domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//import java.util.logging.Logger;

import Dtos.UserDto;

public class User {
    private String _encoded_password;
    private String _username;
    private boolean _isAdmin;
    private String _email;
    private Date _birthDate;
    private List<Order> _purchaseHistory;
    //private static final Logger logger = Logger.getLogger(UserFacade.class.getName());

    public User(String username, String encoded_password, String email, Date birthDate) {
        _username = username;
        _encoded_password = encoded_password;
        _isAdmin = false;
        _email = email;
        _birthDate = birthDate;
        _purchaseHistory = new ArrayList<Order>();
    }

    public User(UserDto userDto) {
        _username = userDto.username;
        _encoded_password = userDto.password;
        _isAdmin = false;
        _email = userDto.email;
        _birthDate = userDto.birthDate;
        _purchaseHistory = new ArrayList<Order>();
    }

    public String getEncodedPassword() {
        return _encoded_password;
    }

    public boolean isCurrUser(String username, String encoded_password) {
        if (_username == username & _encoded_password == encoded_password) {
            return true;
        }
        return false;
    }

    //add order to the purchase history
    public void addOrder(Order order) {
        if (order == null) {
            //logger.severe("Order is null");
            throw new IllegalArgumentException("Order is null");
            
        }
        _purchaseHistory.add(order);
    }

    /**
     * Checks if the user is an admin.
     *
     * @return A boolean indicating whether the user is an admin.
     */
    public boolean isAdmin() {
        return _isAdmin;
    }

    /**
     * Retrieves the user's purchase history.
     *
     * @return A list of ShoppingBasket objects representing the user's purchase
     *         history.
     */
    public List<Order> getPurchaseHistory() {
        return _purchaseHistory;
    }

    // check id age above some age for policies
    public boolean checkAgeAbove(int age) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(_birthDate);
        
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int birthYear = birthCalendar.get(Calendar.YEAR);
        
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int birthMonth = birthCalendar.get(Calendar.MONTH);
        
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int birthDay = birthCalendar.get(Calendar.DAY_OF_MONTH);
        
        int userAge = currentYear - birthYear;
        
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            userAge--;
        }
        
        return userAge > age;
    }

    // getters and setters:

    public String getUserName() {
        return _username;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
        }

    public String getPassword() {
        return _encoded_password;
    }

    public void setPassword(String password) {
        _encoded_password = password;
    }

    public Date getBirthDate() {
        return _birthDate;
    }

    public void setBirthDate(Date birthDate) {
        _birthDate = birthDate;
    }

    public String toString() {
        return "User [username=" + _username + ", encoded password= " + _encoded_password + ", email=" + _email + ", birth date=" + _birthDate.toString() + "]";
    }

    public void setIsSystemAdmin(boolean b) {
        _isAdmin = true;
    }
}
