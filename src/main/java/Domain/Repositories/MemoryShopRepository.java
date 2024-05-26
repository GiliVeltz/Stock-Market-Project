package Domain.Repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Domain.Shop;

public class MemoryShopRepository implements ShopRepositoryInterface {
    private Map<Integer, Shop> _shops;

    public MemoryShopRepository(List<Shop> shops) {
        _shops = new HashMap<>();
        for (Shop shop : shops)
            _shops.put(shop.getShopId(), shop);
    }

    @Override
    public boolean doesShopExist(int shopID) {
        return _shops.containsKey(shopID);
    }

    @Override
    public Shop getShopByID(int shopID) {
        return _shops.getOrDefault(shopID, null);
    }

    @Override
    public void addShop(Shop shop) {
        _shops.put(shop.getShopId(), shop);
    }

    @Override
    public List<Shop> getAllShops() {
        return new ArrayList<>(_shops.values());
    }

}
