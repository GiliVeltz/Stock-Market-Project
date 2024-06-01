package Dtos;

import java.util.Date;

public class UserDto {
    public String username;
    public String password;
    public String email;
    public Date birthDate;

    public UserDto(String username, String password, String email, Date birthDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
    }
}
