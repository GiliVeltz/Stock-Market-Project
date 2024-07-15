package Domain.Entities.Discounts;

import java.util.Date;

import Domain.Entities.Product;
import Domain.Entities.Shop;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.Rules.Rule;
import Dtos.BasicDiscountDto;
import Exceptions.DiscountExpiredException;
import Exceptions.StockMarketException;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discount_type")
public abstract class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer _id;
    
    
    @Transient
    protected Rule<ShoppingBasket> _rule;
    
    @Transient
    protected Rule<Product> _specialRule;

    @Column(name = "expiration_date", nullable = false)
    private Date _expirationDate;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    public Discount(Date expirationDate, int id) {
        _expirationDate = expirationDate;
        _id = id;
    }

    public void applyDiscount(ShoppingBasket basket) throws StockMarketException {
        Date currentTime = new Date();
        if (currentTime.before(_expirationDate) && _rule.predicate(basket))
            applyDiscountLogic(basket);
        else if (currentTime.after(_expirationDate))
            throw new DiscountExpiredException("Discount has expired");
    }

    public Date getExpirationDate() {
        return _expirationDate;
    }

    public int getId() {
        return _id;
    }

    public int setId(int id){
        return _id = id;
    }

    // A special predicate to handle the shop and category discounts
    public boolean specialPredicate(Product prodcut){
        if(_specialRule != null){
            return _specialRule.predicate(prodcut);
        }
        return false;
    }

    public abstract int getParticipatingProduct();
    
    public abstract BasicDiscountDto getDto();

    protected abstract void applyDiscountLogic(ShoppingBasket basket) throws StockMarketException;

    public abstract int getDiscountId();
}
