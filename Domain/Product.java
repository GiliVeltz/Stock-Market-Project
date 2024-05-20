public class Product {
    private Integet m_ProductId;
    private String m_ProductName;
    private double m_Price;

    // Constructor
    public Product(Integer pi_ProductId, String pi_ProductName, double pi_Price) {
        this.m_ProductId = pi_ProductId;
        this.m_ProductName = pi_ProductName;
        this.m_Price = pi_Price;
    }

    public int getProductId() {
        return m_ProductId;
    }

    public String getProductName() {
        return m_ProductName;
    }

    public double getPrice() {
        return m_Price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + m_ProductId +
                ", name='" + m_ProductName + '\'' +
                ", price=" + m_Price +
                '}';
    }
}
