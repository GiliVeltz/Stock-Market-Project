package Domain.Repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import Domain.Entities.Shop;

@Repository
public interface InterfaceShopRepository {
    boolean doesShopExist(int shopID);

    Shop getShopByID(int shopID);

    void addShop(Shop shop);

    List<Shop> getAllShops();

    int getUniqueShopID();

    int getUniqueProductID();
}
