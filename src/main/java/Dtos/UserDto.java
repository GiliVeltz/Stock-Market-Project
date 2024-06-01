package Dtos;

public class UserDto {
    public String username;
    public String password;
    public String email;

    public UserDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
