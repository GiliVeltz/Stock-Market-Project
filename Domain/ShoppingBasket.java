public class ShoppingBasket {
    private Integer m_ShopId;
    private List<Product> m_ProductList;
    private double m_BasketTotalAmount;

    // Constructor
    public ShoppingBasket(Integer pi_ShopId){
        this.m_ShopId = pi_ShopId;
        this.m_ProductList = new ArrayList<>();
        this.m_BasketTotalAmount = 0.0;
    }


    public void addProductToShoppingBasket(Product pi_Product){
        m_ProductList.add(pi_Product);
    }
 
    // Calculate and return the total price of all products in the basket
    public double getShoppingBasketPrice() {
        m_BasketTotalAmount = 0.0; 
        for (Product product : m_ProductList) {
            m_BasketTotalAmount += product.getPrice();
        }
        return m_BasketTotalAmount;
    }

    @Override
    public String toString() {
        return "ShoppingBasket{" +
                "ShopId=" + m_ShopId +
                ", products=" + m_ProductList +
                '}';
    }
}
