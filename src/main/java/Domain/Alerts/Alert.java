package Domain.Alerts;
// Purpose: Interface for the Alert class.
public interface Alert {

    public String getMessage();
    public void setMessage();
    public String getFromUser();
    public boolean isEmpty();
}