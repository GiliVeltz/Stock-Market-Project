package Domain.Repositories;

import Domain.ShoppingCart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public class DbShoppingCartRepository implements InterfaceShoppingCartRepository{
    
    private final JpaRepository<ShoppingCart, String> jpaShoppingCartRepository;
    private int _orderIdCounter;

    @Autowired
    public DbShoppingCartRepository(JpaRepository<ShoppingCart, String> jpaShoppingCartRepository) {
        this.jpaShoppingCartRepository = jpaShoppingCartRepository;
        _orderIdCounter = 0;
    }

    @Override
    public void addCartForUser(String username, ShoppingCart cart) {
        jpaShoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCart getCartByUsername(String username) {
        return jpaShoppingCartRepository.getReferenceById(username);
    }

    @Override
    public synchronized int getUniqueOrderID() {
        return _orderIdCounter++;
    }
}