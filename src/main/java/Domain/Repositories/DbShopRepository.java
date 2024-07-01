package Domain.Repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import Domain.Shop;

@Repository
public class DbShopRepository implements InterfaceShopRepository {
    
    private final JpaRepository<Shop, Integer> jpaShoppingCartRepository;

    private int _shopIdCounter;
    private int _productIdCounter;

    @Autowired
    public DbShopRepository(JpaRepository<Shop, Integer> jpaShoppingCartRepository) {
        this.jpaShoppingCartRepository = jpaShoppingCartRepository;
        _shopIdCounter = 0;
        _productIdCounter = 0;
    }

    @Override
    public boolean doesShopExist(int shopID) {
        return jpaShoppingCartRepository.existsById(shopID);
    }

    @Override
    public Shop getShopByID(int shopID) {
        return jpaShoppingCartRepository.getReferenceById(shopID);
    }

    @Override
    public synchronized void addShop(Shop shop) {
        jpaShoppingCartRepository.save(shop);
    }

    @Override
    public List<Shop> getAllShops() {
        return jpaShoppingCartRepository.findAll();
    }

    @Override
    public synchronized int getUniqueShopID() {
        return _shopIdCounter++;
    }

    @Override
    public synchronized int getUniqueProductID() { return _productIdCounter++;}
}
