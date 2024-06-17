package UI.View;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import UI.Presenter.BasePresenter;

public abstract class BaseView extends VerticalLayout implements BeforeEnterObserver {

    private final BasePresenter presenter;
    private final String _serverPort = "8080";

    public BaseView() {
        this.presenter = new BasePresenter();
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
        return _serverPort;
    }
}
