package Domain.Repositories;

import java.util.List;

import Domain.Shop;

public interface ShopRepositoryInterface {
    boolean doesShopExist(int shopID);

    Shop getShopByID(int shopID);

    void addShop(Shop shop);

    List<Shop> getAllShops();

    int getUniqueShopID();
}
