package Domain.Entities.Policies;

import Domain.Entities.ShoppingBasket;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SHOP_POLICY")
public class ShopPolicy extends Policy<ShoppingBasket>{
    
}
