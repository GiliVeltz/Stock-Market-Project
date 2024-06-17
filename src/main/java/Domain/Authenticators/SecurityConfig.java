package Domain.Authenticators;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorize -> authorize
                .antMatchers("/api/user/login", "/api/user/register").permitAll()
                .anyRequest().authenticated()
            )
            .requiresChannel(requiresChannel -> requiresChannel
                .antMatchers("/api/user/login", "/api/user/register").requiresSecure()
                .anyRequest().requiresInsecure()
            )
            .csrf().disable(); // Disable CSRF for simplicity; enable if needed
    }
}
