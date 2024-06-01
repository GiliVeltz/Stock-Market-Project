package Server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ServiceLayer.ShopService;

@RestController
@RequestMapping(path = "/api/user")
public class ShopAPI {
    private final ShopService _shopService;

    @Autowired
    public ShopAPI(ShopService shopService) {
        this._shopService = shopService;
    }

    
}
