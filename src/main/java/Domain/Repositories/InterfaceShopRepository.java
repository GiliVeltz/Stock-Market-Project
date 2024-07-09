package Domain.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import Domain.Entities.Shop;

@NoRepositoryBean
public interface InterfaceShopRepository extends JpaRepository<Shop, Integer> {
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shop s WHERE s.shopName = ?1")
    int getUniqueShopID();

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shop s WHERE s.shopName = ?1")
    int getUniqueProductID();
}
