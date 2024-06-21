package UI.Presenter;

import UI.Model.ProductDto;

public class dtoWrapper {
    private Integer shopId;
    private ProductDto productDto;

    public dtoWrapper(Integer shopId, ProductDto productDto) {
        this.shopId = shopId;
        this.productDto = productDto;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }
    
    public Integer getShopId() {
        return shopId;
    }

    public ProductDto getProductDto() {
        return productDto;
    }

    public void setProductDto(ProductDto productDto) {
        this.productDto = productDto;
    }
}
