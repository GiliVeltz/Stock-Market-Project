package UI.Model;

import Domain.Entities.enums.Category;

public class CategoryDto {
    public String _categoryName;

    public CategoryDto(String categoryName) {
        this._categoryName = categoryName;
    }
    
    public CategoryDto(Category categoryName) {
        this._categoryName = categoryName.toString();
    }
}
