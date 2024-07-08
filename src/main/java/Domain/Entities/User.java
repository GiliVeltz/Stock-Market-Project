
package Domain.Entities;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Dtos.UserDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
@Table(name = "[user]")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long _id;

    @Column(name = "password", nullable = false)
    private String _password;

    @Column(name = "username", unique = true, nullable = false)
    private String _username;

    @Column(name = "isAdmin", nullable = true)
    private boolean _isAdmin=false;

    @Column(name = "email", unique = true, nullable = false)
    private String _email;

    @Column(name = "birthDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date _birthDate;

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Transient
    private List<Order> _purchaseHistory;

    @Column(name = "isLoggedIn", nullable = true)
    private boolean _isLoggedIn;

    // private static final Logger logger = Logger.getLogger(UserFacade.class.getName());

    public User(){}

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

    public long getId(){
        return _id;
    }
}
