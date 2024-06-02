package Server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Dtos.ShopDto;
import ServiceLayer.Response;
import ServiceLayer.ShopService;

@RestController
@RequestMapping(path = "/api/shop")
public class ShopController {
    private final ShopService _shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this._shopService = shopService;
    }

    @PostMapping("/openNewShop")
    public Response openNewShop(@RequestHeader("Authorization") String token, @RequestBody ShopDto shopDto) {
        return _shopService.openNewShop(token, shopDto);

    }

}
