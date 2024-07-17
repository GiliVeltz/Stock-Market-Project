package Domain.Entities.Rules;

import java.util.List;

import Domain.Entities.ShoppingBasket;

/**
 * Represents a rule that checks if all the items in a list of product IDs are
 * present in a shopping basket.
 * Implements the Rule interface with ShoppingBasket as the generic type.
 */
public class AllItemsRule extends AbstractRule<ShoppingBasket> {
    private List<Integer> _productIds;

    /**
     * Constructs a new AllItemsRule object with the specified list of product IDs.
     * 
     * @param productIds the list of product IDs to check in the shopping basket,
     *                   can contain duplicates.
     */
    public AllItemsRule(List<Integer> productIds) {
        _productIds = productIds;
    }

    private int productCount(int pid) {
        int count = 0;
        for (int otherPid : _productIds) {
            if (otherPid == pid) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if all the products in the list of product IDs are present in the
     * shopping basket.
     * 
     * @param basket the shopping basket to check
     * @return true if all the products are present, false otherwise
     */
    @Override
    public boolean predicate(ShoppingBasket basket) {
        // check if all the products in the list of products are in the basket
        for (Integer pid : _productIds) {
            if (basket.getProductCount(pid) < productCount(pid))
                return false;
        }
        return true;
    }
}
