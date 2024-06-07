package Domain.Alerts;

import java.util.ArrayList;
import java.util.List;

public class Alerts {

    List <Alert> openedAlertsList; //alert list for connected user
    List <Alert> stashedAlerList; //alert list for disconnected user -> will be pop up when user connect

    public Alerts() {
        this.openedAlertsList = new ArrayList<>();
        this.stashedAlerList = new ArrayList<>();
    }
    // Getters and Setters
    public List<Alert> getOpenedAlertsList() {
        return openedAlertsList;
    }

    public List<Alert> getStashedAlertList() {
        return stashedAlerList;
    }

   

}
