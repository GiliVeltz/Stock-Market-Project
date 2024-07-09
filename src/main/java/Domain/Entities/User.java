
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "[user]")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "isAdmin", nullable = true)
    private boolean isAdmin = false;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "birthDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> purchaseHistory;

    @Column(name = "isLoggedIn", nullable = true)
    private boolean isLoggedIn;

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private ShoppingCart shoppingCart;
    
    public User(){}

    // Constructor
    public User(String username, String password, String email, Date birthDate) {
        this.username = username;
        this.password = password;
        isAdmin = false;
        this.email = email;
        this.birthDate = birthDate;
        purchaseHistory = new ArrayList<Order>();
        isLoggedIn = false;
    }

    public User(UserDto userDto) {
        this(userDto.username, userDto.password, userDto.email, userDto.birthDate);
    }

    public String getPassword() {
        return password;
    }

    public boolean isCurrUser(String username, String password) {
        if (this.username == username & this.password == password) {
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
        purchaseHistory.add(order);
    }

    /**
     * Checks if the user is an admin.
     *
     * @return A boolean indicating whether the user is an admin.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Retrieves the user's purchase history.
     *
     * @return A list of ShoppingBasket objects representing the user's purchase
     *         history.
     */
    public List<Order> getPurchaseHistory() {
        return purchaseHistory;
    }

    // check id age above some age for policies
    public boolean checkAgeAbove(int age) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);

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
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String toString() {
        return "User [username=" + username + ", encoded password= " + password + ", email=" + email
                + ", birth date=" + birthDate.toString() + "]";
    }

    public void setIsSystemAdmin(boolean b) {
        isAdmin = true;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void logIn() {
        isLoggedIn = true;
    }

    public void logOut() {
        isLoggedIn = false;
    }

    public int getAge() {
        // Convert _birthDate to LocalDate
        LocalDate birthDateLocal = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        
        // Calculate the difference in years
        return currentDate.getYear() - birthDateLocal.getYear() - (currentDate.getDayOfYear() < birthDateLocal.getDayOfYear() ? 1 : 0);
    }

    public long getId(){
        return id;
    }
}
