package UI.Presenter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;

import UI.View.LandingPageView;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class LandingPagePresenter {

    private final LandingPageView view;

    public LandingPagePresenter(LandingPageView view) {
        this.view = view;
    }

}