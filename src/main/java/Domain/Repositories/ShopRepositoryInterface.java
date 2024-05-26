package Domain.Repositories;

import Domain.Shop;

public interface ShopRepositoryInterface {
    boolean doesShopExist(int shopID);

    Shop getShopByID(int shopID);

    void addShop(Shop shop);
}
