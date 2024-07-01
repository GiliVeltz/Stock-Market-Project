
package Domain;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import Dtos.UserDto;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password", nullable = false)
    private String _password;

    @Column(name = "username", unique = true, nullable = false)
    private String _username;

    @Column(name = "isAdmin", nullable = false)
    private boolean _isAdmin;

    @Column(name = "email", unique = true, nullable = false)
    private String _email;

    @Column(name = "birthDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date _birthDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> _purchaseHistory;

    @Column(name = "isLoggedIn", nullable = false)
    private boolean _isLoggedIn;

    // private static final Logger logger = Logger.getLogger(UserFacade.class.getName());

    public User(String username, String password, String email, Date birthDate) {
        _username = username;
        _password = password;
        _isAdmin = false;
        _email = email;
        _birthDate = birthDate;
        _purchaseHistory = new ArrayList<Order>();
        _isLoggedIn = false;
    }

    public User(UserDto userDto) {
        this(userDto.username, userDto.password, userDto.email, userDto.birthDate);
    }

    public String getPassword() {
        return _password;
    }

    public boolean isCurrUser(String username, String password) {
        if (_username == username & _password == password) {
            return true;
        }
        return false;
    }

    // add order to the purchase history
    public void addOrder(Order order) {
        if (order == null) {
            // logger.severe("Order is null");
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

    public void setPassword(String password) {
        _password = password;
    }

    public Date getBirthDate() {
        return _birthDate;
    }

    public void setBirthDate(Date birthDate) {
        _birthDate = birthDate;
    }

    public String toString() {
        return "User [username=" + _username + ", encoded password= " + _password + ", email=" + _email
                + ", birth date=" + _birthDate.toString() + "]";
    }

    public void setIsSystemAdmin(boolean b) {
        _isAdmin = true;
    }

    public boolean isLoggedIn() {
        return _isLoggedIn;
    }

    public void logIn() {
        _isLoggedIn = true;
    }

    public void logOut() {
        _isLoggedIn = false;
    }

    public int getAge() {
        // Convert _birthDate to LocalDate
        LocalDate birthDateLocal = _birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        
        // Calculate the difference in years
        return currentDate.getYear() - birthDateLocal.getYear() - (currentDate.getDayOfYear() < birthDateLocal.getDayOfYear() ? 1 : 0);
    }
}
