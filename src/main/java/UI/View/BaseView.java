package UI.View;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import UI.Presenter.BasePresenter;

public abstract class BaseView extends VerticalLayout implements BeforeEnterObserver {

    private final BasePresenter presenter;
    // private final String _serverPort = "8080";

    // private final String SERVER_URL = "http://localhost:" + _serverPort;
    private static final String SERVER_PORT = "8080"; // Adjust as needed
    private static final String SERVER_URL = "http://localhost:" + SERVER_PORT;

    public BaseView() {
        this.presenter = new BasePresenter();
         // Inject JavaScript
         injectLeaveSystemScript();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        presenter.checkToken();
    }

    public void showSuccessMessage(String message){
        Notification.show(message);
    }
    public void showErrorMessage(String message){
        Notification.show(message);
    }

    public String getServerPort(){
        return SERVER_PORT;
    }

    public boolean isGuest() {
        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        return username == null;
    }

    private void injectLeaveSystemScript() {
        String script = "window.addEventListener('beforeunload', function (event) {" +
                        "    console.log('beforeunload event triggered');" +
                        "    const token = localStorage.getItem('authToken');" +
                        "    if (token) {" +
                        "        console.log('Sending beacon with token');" +
                        "        navigator.sendBeacon('" + SERVER_URL + "/api/system/leaveSystem', JSON.stringify({ token: token }));" +
                        "    }" +
                        "    event.preventDefault();" +
                        "    event.returnValue = '';" +
                        "});";
        UI.getCurrent().getPage().executeJs(script);
    }
}
