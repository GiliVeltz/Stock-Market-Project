package Domain.Alerts;

import org.springframework.stereotype.Component;

// Purpose: Interface for the Alert class.
@Component
public interface Alert {

    public String getMessage();
    public void setMessage();
    public String getFromUser();
    public boolean isEmpty();
    public int getShopId();
    public String getTargetUser();
}