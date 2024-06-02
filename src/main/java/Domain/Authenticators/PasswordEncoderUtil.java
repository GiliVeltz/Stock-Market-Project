package Domain.Authenticators;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil{
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordEncoderUtil(){
        
    }

    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String decodePassword(String password){
        return passwordEncoder.encode(password);
    }
}