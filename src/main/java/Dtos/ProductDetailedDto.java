package Dtos;

import java.util.HashSet;
import java.util.Map;

//Just like productGetterDto but without DTO objects(Discount, policy).

public class ProductDetailedDto {
@SuppressWarnings("unused")
    private Integer _productId;
    @SuppressWarnings("unused")
    private String _productName;
    @SuppressWarnings("unused")
    private double _price;
    @SuppressWarnings("unused")
    private Integer _quantity;
    @SuppressWarnings("unused")
    private HashSet<String> _keywords;
    @SuppressWarnings("unused")
    private Double _productRating;
    @SuppressWarnings("unused")
    private Integer _productRatersCounter;
    @SuppressWarnings("unused")
    private CategoryDto _category;
    @SuppressWarnings("unused")
    private Map<String, String> _reviews; // usernames and reviews
}
