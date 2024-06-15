package UI.View;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import UI.Presenter.BasePresenter;

public abstract class BaseView extends VerticalLayout implements BeforeEnterObserver {

    private final BasePresenter presenter;

    public BaseView() {
        this.presenter = new BasePresenter();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        presenter.checkToken();
    }
}
