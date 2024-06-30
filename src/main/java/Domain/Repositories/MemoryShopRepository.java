package Domain.Repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Domain.Shop;

import org.springframework.stereotype.Repository;

@Repository
public class MemoryShopRepository implements InterfaceShopRepository {
    private Map<Integer, Shop> _shops;
    private int _shopIdCounter;
    private int _productIdCounter;

    public MemoryShopRepository(List<Shop> shops) {
        _shops = new HashMap<>();
        _shopIdCounter = 0;
        _productIdCounter = 0;
        for (Shop shop : shops) {
            _shops.put(shop.getShopId(), shop);
            _shopIdCounter++;
            _productIdCounter += shop.getAmoutOfProductInShop();
        }
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
    public synchronized void addShop(Shop shop) {
        _shops.put(shop.getShopId(), shop);
    }

    @Override
    public List<Shop> getAllShops() {
        return new ArrayList<>(_shops.values());
    }

    @Override
    public synchronized int getUniqueShopID() {
        return _shopIdCounter++;
    }

    @Override
    public synchronized int getUniqueProductID() { return _productIdCounter++;}

}
