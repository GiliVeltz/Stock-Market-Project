package Dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import enums.Category;

public class CategoryDto {
    public String _categoryName;

   @JsonCreator
    public CategoryDto(@JsonProperty("categoryName") String categoryName) {
        this._categoryName = categoryName;
    }

    // public CategoryDto(String categoryName) {
    //     this._categoryName = categoryName;
    // }
    
    public CategoryDto(Category categoryName) {
        this._categoryName = categoryName.toString();
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        this._categoryName = categoryName;
    }
}
