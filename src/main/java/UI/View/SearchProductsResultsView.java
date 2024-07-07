package UI.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import Domain.Shop;
import UI.Model.ProductDto;
import UI.Model.SearchProductResponseDto;
import UI.Model.ShopDto;
import UI.Presenter.SearchProductsPresenter;

@PageTitle("Search Products Results Page")
@Route(value = "products_search_results")
public class SearchProductsResultsView extends BaseView {
    //private SearchProductsPresenter presenter;
    Dialog resultsDialog;
    VerticalLayout resultLayout;
    private final List<VerticalLayout> shopLayoutsList;
    private Map<ShopDto, List<ProductDto>> shopToProductsMap;
    private Map<ShopDto, VerticalLayout> shopLayoutMap;
    private Map<ProductDto, Button> productButtonMap;
    private boolean isAnyProductFound;

    public SearchProductsResultsView(SearchProductsPresenter presenter) {
        // Initialize presenter
        //this.presenter = presenter;
        this.shopLayoutsList = new ArrayList<>();
        resultsDialog = new Dialog();
        resultsDialog.setWidth("1000px");
        resultLayout = new VerticalLayout();
        shopToProductsMap = new HashMap<>();
        shopLayoutMap = new HashMap<>();
        productButtonMap = new HashMap<>();
        isAnyProductFound = false;
    }

    public void displayResponseProducts (Map<String, List<ProductDto>> shopStringToProducts) {
        clearSearchResults();  // Clear previous search results

        // create vertical Layout for the search results
        VerticalLayout dialogContent = new VerticalLayout();

        // Add "Products Search Results" title
        H2 headline = new H2("Products Search Results");
        headline.getStyle().set("margin", "0");

        boolean isMoreThanOneShop = shopStringToProducts.size() > 1;

        VerticalLayout filtersLayout = createFiltersSideBar(isMoreThanOneShop);
        filtersLayout.setWidth("0%");
        filtersLayout.setVisible(false);
        filtersLayout.getStyle().set("margin-left", "0");
        filtersLayout.getStyle().set("padding-left", "0");
        
        resultLayout.setWidth("100%");

        createShopProductsMap(shopStringToProducts);
        createShopLayoutMap();

        // Add the shop layouts to the main layout
        for (VerticalLayout shopLayout : shopLayoutsList) {
            resultLayout.add(shopLayout);
        }
         // Add close button
         Button closeButton = new Button("Close");
         closeButton.addClickListener(event -> {
             clearSearchResults();
             resultsDialog.close();
         });
         closeButton.addClassName("pointer-cursor");

        HorizontalLayout dialogBodyLayout = new HorizontalLayout();
        dialogBodyLayout.setWidthFull();
        dialogBodyLayout.add(filtersLayout, resultLayout);

        Button toggleFiltersButton = new Button("Show Filters");
        toggleFiltersButton.addClassName("pointer-cursor");
        toggleFiltersButton.addClickListener(event -> {
            if (filtersLayout.isVisible()) {
                filtersLayout.setWidth("0%");
                resultLayout.setWidth("100%");
                toggleFiltersButton.setText("Show Filters");
                filtersLayout.setVisible(false);  // Hide filters
            } else {
                filtersLayout.setWidth("20%");  
                resultLayout.setWidth("80%");
                toggleFiltersButton.setText("Hide Filters");
                filtersLayout.setVisible(true); // Show filters
            }            
        });
        toggleFiltersButton.setVisible(isAnyProductFound);
        if (isAnyProductFound) {
            resultsDialog.setHeight("600px"); // Set a fixed height for the dialog
            resultsDialog.getElement().getStyle().set("overflow", "auto"); // Ensure the dialog content is scrollable if needed
        }
        dialogContent.add(headline, toggleFiltersButton, dialogBodyLayout, closeButton);
        dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
        resultsDialog.add(dialogContent);
        resultsDialog.open();

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private VerticalLayout createFiltersSideBar(boolean isMoreThanOneShop) {
        VerticalLayout filtersLayout = new VerticalLayout();
        boolean[] areFiltersPickedAndValid = {false, false, false, false}; // Price Range, Category, Product Rating, Shop Rating

        // Product Price Range Filter
        H5 priceRangeLabel = new H5("⬜Price Range");
        priceRangeLabel.addClassName("filter-headline");
        priceRangeLabel.addClassName("pointer-cursor");
        Span priceRangeErrorMessage = new Span("Price range is not valid.");
        priceRangeErrorMessage.addClassName("input-error-message");
        priceRangeErrorMessage.setVisible(false);
        NumberField minPrice = new NumberField("Min Price");
        minPrice.setWidthFull();
        NumberField maxPrice = new NumberField("Max Price");
        maxPrice.setWidthFull();
        VerticalLayout priceRangeLayout = new VerticalLayout(priceRangeLabel, priceRangeErrorMessage, minPrice, maxPrice);
        priceRangeLayout.addClassName("filter-container");
        priceRangeLabel.addClickListener(listener -> {
            if (!areFiltersPickedAndValid[0]) {
                if (minPrice.isEmpty() || maxPrice.isEmpty() || minPrice.getValue() > maxPrice.getValue()) {
                    areFiltersPickedAndValid[0] = false;
                    priceRangeErrorMessage.setVisible(true);
                } else {
                    areFiltersPickedAndValid[0] = true;
                    priceRangeErrorMessage.setVisible(false);
                    minPrice.setReadOnly(true);
                    maxPrice.setReadOnly(true);
                    priceRangeLabel.setText("☑️Price Range");
                }
            } else {
                unclickFilter(0, areFiltersPickedAndValid, priceRangeErrorMessage, null, priceRangeLabel);
                minPrice.setReadOnly(false);
                maxPrice.setReadOnly(false);
            }
        });

        // Product Category Filter
        H5 categoryLabel = new H5("⬜Category");
        categoryLabel.addClassName("filter-headline");
        categoryLabel.addClassName("pointer-cursor");
        Span categoryErrorMessage = new Span("You must pick a category.");
        categoryErrorMessage.addClassName("input-error-message");
        categoryErrorMessage.setVisible(false);
        CheckboxGroup<String> categoryGroup = new CheckboxGroup<>();
        categoryGroup.setItems("Electronics", "Clothing", "Home Appliances", "Books");
        VerticalLayout categoryLayout = new VerticalLayout(categoryLabel, categoryErrorMessage, categoryGroup);
        categoryLayout.addClassName("filter-container");
        categoryLabel.addClickListener(listener -> {
            if (!areFiltersPickedAndValid[1]) {
                if (categoryGroup.getValue().isEmpty()) {
                    areFiltersPickedAndValid[1] = false;
                    categoryErrorMessage.setVisible(true);
                } else {
                    areFiltersPickedAndValid[1] = true;
                    categoryErrorMessage.setVisible(false);
                    categoryGroup.setReadOnly(true);
                    categoryLabel.setText("☑️Category");
                }
            } else {
                unclickFilter(1, areFiltersPickedAndValid, categoryErrorMessage, categoryGroup, categoryLabel);
            }
        });

       // Product Rating Filter
        H5 productRatingLabel = new H5("⬜Product Rating");
        productRatingLabel.addClassName("filter-headline");
        productRatingLabel.addClassName("pointer-cursor");
        Span productRatingErrorMessage = new Span("You must pick a rating.");
        productRatingErrorMessage.addClassName("input-error-message");
        productRatingErrorMessage.setVisible(false);
        CheckboxGroup<String> productRatingGroup = new CheckboxGroup<>();
        productRatingGroup.setItems("No Rating", "⭐", "⭐⭐", "⭐⭐⭐", "⭐⭐⭐⭐", "⭐⭐⭐⭐⭐");
        VerticalLayout productRatingLayout = new VerticalLayout(productRatingLabel, productRatingErrorMessage, productRatingGroup);
        productRatingLayout.addClassName("filter-container");
        productRatingLabel.addClickListener(listener -> {
            if (!areFiltersPickedAndValid[2]) {
                if (productRatingGroup.getValue().isEmpty()) {
                    areFiltersPickedAndValid[2] = false;
                    productRatingErrorMessage.setVisible(true);
                } else {
                    areFiltersPickedAndValid[2] = true;
                    productRatingErrorMessage.setVisible(false);
                    productRatingGroup.setReadOnly(true);
                    productRatingLabel.setText("☑️Product Rating");
                }
            } else {
                unclickFilter(2, areFiltersPickedAndValid, productRatingErrorMessage, productRatingGroup, productRatingLabel);
                // areFiltersPickedAndValid[2] = false;
                // productRatingErrorMessage.setVisible(false);
                // productRatingGroup.setReadOnly(false);
                // productRatingLabel.setText("⬜Product Rating");
            }
        });

        // Shop Rating Filter
        H5 shopRatingLabel = new H5("⬜Shop Rating");
        shopRatingLabel.addClassName("filter-headline");
        shopRatingLabel.addClassName("pointer-cursor");
        Span shopRatingErrorMessage = new Span("You must pick a rating.");
        shopRatingErrorMessage.addClassName("input-error-message");
        shopRatingErrorMessage.setVisible(false);
        CheckboxGroup<String> shopRatingGroup = new CheckboxGroup<>();
        shopRatingGroup.setItems("No Rating", "⭐", "⭐⭐", "⭐⭐⭐", "⭐⭐⭐⭐", "⭐⭐⭐⭐⭐");
        VerticalLayout shopRatingLayout = new VerticalLayout(shopRatingLabel, shopRatingErrorMessage, shopRatingGroup);
        shopRatingLayout.addClassName("filter-container");

        shopRatingLabel.addClickListener(event -> {
            if (!areFiltersPickedAndValid[3]) {
                if (shopRatingGroup.getValue().isEmpty()) {
                    areFiltersPickedAndValid[3] = false;
                    shopRatingErrorMessage.setVisible(true);
                } else {
                    areFiltersPickedAndValid[3] = true;
                    shopRatingErrorMessage.setVisible(false);
                    shopRatingGroup.setReadOnly(true);
                    shopRatingLabel.setText("☑️Shop Rating");
                }
            } else {
                unclickFilter(3, areFiltersPickedAndValid, shopRatingErrorMessage, shopRatingGroup, shopRatingLabel);
                // areFiltersPickedAndValid[3] = false;
                // shopRatingErrorMessage.setVisible(false);
                // shopRatingGroup.setReadOnly(false);
                // shopRatingLabel.setText("⬜Shop Rating");
            }
        });
        shopRatingLayout.setVisible(isMoreThanOneShop);

        // Apply Filters Button
        Button applyFiltersButton = new Button("Apply Filters");
        applyFiltersButton.addClickListener(event -> {
            Double miniPrice = null;
            Double maxiPrice = null;
            List<String> categories = null;
            List<Integer> productRatings = null;
            List<Integer> shopRatings = null;
            if (!areFiltersPickedAndValid[0] && !areFiltersPickedAndValid[1] && !areFiltersPickedAndValid[2] && !areFiltersPickedAndValid[3]) {
                clearAllFilters();
            }
            else {
                if (areFiltersPickedAndValid[0]) {
                    miniPrice = minPrice.getValue();
                    maxiPrice = maxPrice.getValue();
                }
                if (areFiltersPickedAndValid[1]) {
                    if (categoryGroup.getValue() != null && !categoryGroup.getValue().isEmpty()) {
                        categories = new ArrayList<>();
                        for (String category : categoryGroup.getValue()) {
                            categories.add(category.toUpperCase());
                        }
                    }
                }
                if (areFiltersPickedAndValid[2]) {
                    productRatings = convertRatingStringToInt(productRatingGroup.getValue());
                }
                if (areFiltersPickedAndValid[3]) {
                    shopRatings = convertRatingStringToInt(shopRatingGroup.getValue());
                }
                applyFilters(miniPrice, maxiPrice, categories, productRatings, shopRatings);
            }
        });
        applyFiltersButton.addClassName("pointer-cursor");
        applyFiltersButton.addClassName("filter-container");
        // Placeholder for filter logic to be implemented later

        // Clear Filters Button
        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.addClickListener(event -> {
            minPrice.clear();
            maxPrice.clear();
            categoryGroup.clear();
            productRatingGroup.clear();
            shopRatingGroup.clear();
            minPrice.setReadOnly(false);
            maxPrice.setReadOnly(false);
            unclickFilter(0, areFiltersPickedAndValid, priceRangeErrorMessage, null, priceRangeLabel);
            unclickFilter(1, areFiltersPickedAndValid, categoryErrorMessage, categoryGroup, categoryLabel);
            unclickFilter(2, areFiltersPickedAndValid, productRatingErrorMessage, productRatingGroup, productRatingLabel);
            unclickFilter(3, areFiltersPickedAndValid, shopRatingErrorMessage, shopRatingGroup, shopRatingLabel);
        });
        clearFiltersButton.addClassName("pointer-cursor");
        clearFiltersButton.addClassName("filter-container");

        // Add all filters and buttons to the layout
        filtersLayout.add(priceRangeLayout, categoryLayout, productRatingLayout, shopRatingLayout, applyFiltersButton, clearFiltersButton);
        filtersLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, applyFiltersButton, clearFiltersButton);

        return filtersLayout;

    }

    private List<Integer> convertRatingStringToInt(Set<String> ratingStrings) {
        List<Integer> ratings = new ArrayList<>();
        for (String rating : ratingStrings) {
            switch (rating) {
                case "No Rating":
                    ratings.add(-1);
                    break;
                case "⭐":
                    ratings.add(1);
                    break;
                case "⭐⭐":
                    ratings.add(2);
                    break;
                case "⭐⭐⭐":
                    ratings.add(3);
                    break;
                case "⭐⭐⭐⭐":
                    ratings.add(4);
                    break;
                case "⭐⭐⭐⭐⭐":
                    ratings.add(5);
                    break;
            }
        }
        return ratings;
    }

    private void unclickFilter(int index, boolean[] areFiltersPickedAndValid, Span errorMessage, CheckboxGroup<String> group, H5 label) {
        areFiltersPickedAndValid[index] = false;
        errorMessage.setVisible(false);
        if (group != null) {
            group.setReadOnly(false);
        }
        label.setText("⬜" + label.getText().substring(1));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Create the map of shops to products
     */
    public void createShopProductsMap(Map<String, List<ProductDto>> shopStringToProducts) {
        clearSearchResults();
        if (shopStringToProducts.isEmpty()) {
            shopLayoutsList.add(createNoProductsShopLayout(true, null));
        }
        else {
            for (Map.Entry<String, List<ProductDto>> entry : shopStringToProducts.entrySet()) {
                shopToProductsMap.put(new ShopDto(entry.getKey()), entry.getValue());
            }
        }
    }

    /*
     * Create the map of shopDto to layout
     */
    public void createShopLayoutMap() {
        shopLayoutMap.clear();
        for (Map.Entry<ShopDto, List<ProductDto>> entry : shopToProductsMap.entrySet()) {
            if (entry.getValue().isEmpty()) {
                VerticalLayout noResultsInShopLayout = createNoProductsShopLayout(true, entry.getKey());
                shopLayoutMap.put(entry.getKey(), noResultsInShopLayout);
                shopLayoutsList.add(noResultsInShopLayout);
            } else {
                VerticalLayout shopLayout = createInitialShopLayout(entry.getKey());
                shopLayoutMap.put(entry.getKey(), shopLayout);
                shopLayoutsList.add(shopLayout);
            }
        }
        
    }

    /*
     * Create a ShopLayout with no products
     */
    public VerticalLayout createNoProductsShopLayout (boolean isExist, ShopDto shop) {
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);
        gridLayout.addClassName("light-component-container");
        H3 shopNameLabel = new H3();
        if (shop == null) {
            shopNameLabel.add("All Shops");
        }
        // Create a vertical layout for the grid
        else {
            shopNameLabel.add(shop.getShopName());
        }
        shopNameLabel.addClassName("shop-name-label");
        gridLayout.add(shopNameLabel);

        H5 noResultsLabel = new H5();
        if (isExist) {
            noResultsLabel.add("No results were found.");
        } else {
            noResultsLabel.add("Shop with the given name does not exist, please try again.");
        }
        gridLayout.add(noResultsLabel);

        // Add the grid layout to the main layout
        gridLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        return gridLayout;
    }

    /*
     * Create a ShopLayout with products
     * call createProductButtonMap
     */
    private VerticalLayout createInitialShopLayout(ShopDto shop) {
        isAnyProductFound = true;
        
        List<ProductDto> productsList = shopToProductsMap.get(shop);

        // Create a vertical layout for the grid
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);
        gridLayout.addClassName("light-component-container");

        H3 shopNameLabel = new H3(shop.getShopName());
        Double shopRating = shop.getShopRating();
        H5 shopRatingLabel = new H5();
        if (shopRating >= 0) {
            shopRatingLabel.add("Shop Rating: " + shopRating);
        }
        else {
            shopRatingLabel.add("Shop Rating: shop has no rating yet");
        }
        gridLayout.add(shopNameLabel, shopRatingLabel);
        
        // Set a maximum of 3 buttons per row
        int maxButtonsPerRow = 3;
        HorizontalLayout rowLayout = new HorizontalLayout();
        int count = 0;

        createProductButtonMap(productsList);

        for (ProductDto product : productsList) {
            rowLayout.add(productButtonMap.get(product));
            if ((count + 1) % maxButtonsPerRow == 0 || count == productsList.size() - 1) {
                gridLayout.add(rowLayout);
                rowLayout = new HorizontalLayout();
            }
            count++;
        }
        // Add the grid layout to the main layout
        gridLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        return gridLayout;
    }


    /*
     * Create a map of productDto to button
     */
    private void createProductButtonMap (List<ProductDto> productsList) {
        for (ProductDto product : productsList) {
            Button productButton = new Button(product.getProductName());  // Display product name
            productButton.addClassName("product-button");
            productButton.addClassName("pointer-cursor");
            productButton.addClickListener(event -> {
            showProductDialog(product);  // Open a dialog with product details
            });
            productButtonMap.put(product, productButton);
        }
    }

    public void applyFilters(Double minPrice, Double maxPrice, List<String> categories, List<Integer> productRatings, List<Integer> shopRatings) {
        setVisibleFalseBeforeFilters();
        if (minPrice != null && maxPrice != null) {
           for (Map.Entry<ShopDto, List<ProductDto>> entry : shopToProductsMap.entrySet()) {
               for (ProductDto product : entry.getValue()) {
                   if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                       productButtonMap.get(product).setVisible(true);
                   }
               }
           }
        }
        if (categories != null && !categories.isEmpty()) {
            for (Map.Entry<ShopDto, List<ProductDto>> entry : shopToProductsMap.entrySet()) {
                for (ProductDto product : entry.getValue()) {
                    if (categories.contains(product.getCategory().toString())) {
                        productButtonMap.get(product).setVisible(true);                    }
                }
            }
        }
        if (productRatings != null && !productRatings.isEmpty()) {
            for (Map.Entry<ShopDto, List<ProductDto>> entry : shopToProductsMap.entrySet()) {
                for (ProductDto product : entry.getValue()) {
                    if (productRatings.contains((int) product.getProductRating())) {
                        productButtonMap.get(product).setVisible(true);                    }
                }
            }
        }
        for (ShopDto shop : shopToProductsMap.keySet()) {
            if (shopRatings != null && !shopRatings.isEmpty() && !shopRatings.contains((int)shop.getShopRating())) {
                shopLayoutMap.get(shop).setVisible(false);
            } else {
                shopLayoutMap.get(shop).setVisible(true);
            }
        }
        //applyFiltersOnResultLayout();
    }

    private void setVisibleFalseBeforeFilters() {
        for (ProductDto product : productButtonMap.keySet()) {
            productButtonMap.get(product).setVisible(false);
        }
        for (ShopDto shop : shopLayoutMap.keySet()) {
            shopLayoutMap.get(shop).setVisible(false);
        }
    }

    private void clearAllFilters() {
        for (ProductDto product : productButtonMap.keySet()) {
            productButtonMap.get(product).setVisible(true);
        }
        for (ShopDto shop : shopLayoutMap.keySet()) {
            shopLayoutMap.get(shop).setVisible(true);
        }
    }


    private void applyFiltersOnResultLayout() {
        resultLayout.removeAll();
        for (ShopDto shop : shopLayoutMap.keySet()) {
            if (shopLayoutMap.get(shop).isVisible()) {
                reArrangeShopLayoutAndProductButtonsWhenFiltered(shop);
                resultLayout.add(shopLayoutMap.get(shop));
            }
        }
    }
    /*
     * Re-arrange the product buttons in page when a filter is applied
     */
    public void reArrangeShopLayoutAndProductButtonsWhenFiltered(ShopDto shop) {
        VerticalLayout gridLayout = shopLayoutMap.get(shop);
        gridLayout.setVisible(false);
        gridLayout.removeAll();
        List<ProductDto> productsList = shopToProductsMap.get(shop);

        // Create a vertical layout for the grid
        gridLayout.setAlignItems(Alignment.START);
        gridLayout.addClassName("light-component-container");

        H3 shopNameLabel = new H3(shop.getShopName());
        Double shopRating = shop.getShopRating();
        H5 shopRatingLabel = new H5();
        if (shopRating >= 0) {
            shopRatingLabel.add("Shop Rating: " + shopRating);
        }
        else {
            shopRatingLabel.add("Shop Rating: shop has no rating yet");
        }
        gridLayout.add(shopNameLabel, shopRatingLabel);
        
        // Set a maximum of 3 buttons per row
        int maxButtonsPerRow = 3;
        HorizontalLayout rowLayout = new HorizontalLayout();
        int count = 0;

        for (ProductDto product : productsList) {
            if (productButtonMap.get(product).isVisible()) {
                gridLayout.setVisible(true);
                rowLayout.add(productButtonMap.get(product));
                if ((count + 1) % maxButtonsPerRow == 0 || count == productsList.size() - 1) {
                    gridLayout.add(rowLayout);
                    rowLayout = new HorizontalLayout();
                }
                count++;
            }
        }
        // Add the grid layout to the main layout
        gridLayout.setAlignItems(FlexComponent.Alignment.CENTER);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    public void displayResponseShopNotFound (String shopName) {
        clearSearchResults();  // Clear previous search results

        // create vertical Layout for the search results
        VerticalLayout dialogContent = new VerticalLayout();

        // Add "Products Search Results" title
        H2 headline = new H2("Products Search Results");
        headline.getStyle().set("margin", "0");
        dialogContent.add(headline);

        createNoResultsLayout(false, shopName);

        // Add the shop layouts to the main layout
        for (VerticalLayout shopLayout : shopLayoutsList) {
            dialogContent.add(shopLayout);
        }
         // Add close button
         Button closeButton = new Button("Close");
         closeButton.addClickListener(event -> {
             // Handle close button click
             clearSearchResults();
             resultsDialog.close();
         });
         closeButton.addClassName("pointer-cursor");
         dialogContent.add(closeButton);

         dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
         resultsDialog.add(dialogContent);
         resultsDialog.open();
    }
    
    // public void displayResponseProducts (Map<String, List<ProductDto>> shopStringToProducts) {
    //     //clearSearchFilters();   // to delete the other one when inplemented
    //     clearSearchResults();  // Clear previous search results

    //     // create vertical Layout for the search results
    //     VerticalLayout dialogContent = new VerticalLayout();

    //     // Add "Products Search Results" title
    //     H2 headline = new H2("Products Search Results");
    //     headline.getStyle().set("margin", "0");

    //     VerticalLayout filtersLayout = new VerticalLayout();
    //     filtersLayout.setWidth("0%");
    //     filtersLayout.add(new Div(new Text("filters placeHolder")));
    //     //boolean[] isFiltersShown = {false};
    //     filtersLayout.setVisible(false);

    //     VerticalLayout resultLayout = new VerticalLayout();
    //     resultLayout.setWidth("100%");

    //     if (shopStringToProducts.isEmpty()) {
    //         createNoResultsLayout(true, "All Shops");
    //     }

    //     for (Map.Entry<String, List<ProductDto>> entry : shopStringToProducts.entrySet()) {
    //         if (entry.getValue().isEmpty()) {
    //             createNoResultsLayout(true, SearchProductResponseDto.extractValue(entry.getKey(), "Name"));
    //         } else {
    //             createShopLayout(entry.getKey(), entry.getValue());
    //         }
    //     }
    //     // Add the shop layouts to the main layout
    //     for (VerticalLayout shopLayout : shopLayoutsList) {
    //         resultLayout.add(shopLayout);
    //     }
    //      // Add close button
    //      Button closeButton = new Button("Close");
    //      closeButton.addClickListener(event -> {
    //          // Handle close button click
    //          clearSearchResults();
    //          resultsDialog.close();
    //      });
    //      closeButton.addClassName("pointer-cursor");

    //     HorizontalLayout dialogBodyLayout = new HorizontalLayout();
    //     dialogBodyLayout.setWidthFull();
    //     dialogBodyLayout.add(filtersLayout, resultLayout);

    //     Button toggleFiltersButton = new Button("Show Filters");
    //     toggleFiltersButton.addClassName("pointer-cursor");
    //     toggleFiltersButton.addClickListener(event -> {
    //         if (filtersLayout.isVisible()) {
    //             filtersLayout.setWidth("0%");
    //             resultLayout.setWidth("100%");
    //             toggleFiltersButton.setText("Show Filters");
    //             filtersLayout.setVisible(false);  // Hide filters
    //             //isFiltersShown[0] = false;
    //         } else {
    //             filtersLayout.setWidth("20%");  
    //             resultLayout.setWidth("80%");
    //             toggleFiltersButton.setText("Hide Filters");
    //             filtersLayout.setVisible(true); // Show filters
    //             //isFiltersShown[0] = true;
    //         }            
    //     });

    //     dialogContent.add(headline, toggleFiltersButton, dialogBodyLayout, closeButton);
    //     dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
    //     resultsDialog.add(dialogContent);
    //     resultsDialog.open();
    // }

    private void createShopLayout(String shopString, List<ProductDto> productsList) {
        // Create a vertical layout for the grid
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);
        gridLayout.addClassName("light-component-container");

        H3 shopNameLabel = new H3(SearchProductResponseDto.extractValue(shopString, "Name"));
        Double shopRating = Double.valueOf(SearchProductResponseDto.extractValue(shopString, "Rating"));
        H5 shopRatingLabel = new H5();
        if (shopRating >= 0) {
            shopRatingLabel.add("Shop Rating: " + shopRating);
        }
        else {
            shopRatingLabel.add("Shop Rating: shop has no rating yet");
        }
        gridLayout.add(shopNameLabel, shopRatingLabel);
        
        // Set a maximum of 3 buttons per row
        int maxButtonsPerRow = 3;
        HorizontalLayout rowLayout = new HorizontalLayout();
        int count = 0;

        for (ProductDto product : productsList) {
            Button productButton = createProductInShopButton(product);
            productButton.addClassName("product-button");
            productButton.addClassName("pointer-cursor");
            rowLayout.add(productButton);
            if ((count + 1) % maxButtonsPerRow == 0 || count == productsList.size() - 1) {
                gridLayout.add(rowLayout);
                rowLayout = new HorizontalLayout();
            }

            count++;
        }

        // Add the grid layout to the main layout
        gridLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        shopLayoutsList.add(gridLayout);
    }

    private void createNoResultsLayout(Boolean isExist, String shopName) {
        // Create a vertical layout for the grid
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.START);
        gridLayout.addClassName("light-component-container");

        H3 shopNameLabel = new H3(shopName);
        shopNameLabel.addClassName("shop-name-label");
        gridLayout.add(shopNameLabel);

        H5 noResultsLabel = new H5();
        if (isExist) {
            noResultsLabel.add("No results were found.");
        } else {
            noResultsLabel.add("Shop with the given name does not exist, please try again.");
        }
        gridLayout.add(noResultsLabel);

        // Add the grid layout to the main layout
        gridLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        shopLayoutsList.add(gridLayout);
    }


    private Button createProductInShopButton(ProductDto product) {

        Button productButton = new Button(product.getProductName());  // Display product name
        productButton.addClassName("product-button");

        productButton.addClickListener(event -> {
            showProductDialog(product);  // Open a dialog with product details
        });

        return productButton;
    }

    public void showProductDialog(ProductDto product) {
        // Create a dialog with shop details
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        // Set the dialog content
        VerticalLayout dialogContent = new VerticalLayout();
        String productName = product.getProductName().substring(0, 1).toUpperCase() + product.getProductName().substring(1);
        dialogContent.add(new H3(productName));
        dialogContent.add(new Div());
        dialogContent.add(new Span("Category: " + product.getCategory()));
        dialogContent.add(new Span("Price: " + product.getPrice()));
        double productRating = product.getProductRating();
        if (productRating >= 0) {
            dialogContent.add(new Span("Rating: " + productRating));
        } else {
            dialogContent.add(new Span("Rating: product has no rating yet"));
        }
        dialogContent.add(new Div());


        Button addToCartButton = new Button("Add To Cart", event -> addToCart(product));
        addToCartButton.addClassName("pointer-cursor");
        addToCartButton.setWidth("150px");

        Button produtPageButton = new Button("Product Page", event -> {
            dialog.close();
            navigateToProductDetails(product);
        });
        produtPageButton.addClassName("pointer-cursor");
        produtPageButton.setWidth("150px");

        HorizontalLayout productButtonsLayout = new HorizontalLayout(produtPageButton, addToCartButton);
        dialogContent.add(productButtonsLayout);

        Button closeButton = new Button("Close", event -> dialog.close());
        closeButton.addClassName("pointer-cursor");
        dialogContent.add(closeButton);
        
        dialogContent.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogContent);
        dialog.open();
    }

    private void addToCart(ProductDto product) {
        // Implement logic to add the product to the cart (not shown here)
        // Example: presenter.addToCart(product);
        Notification.show(product.getProductName() + " Add To Cart is not Implemented yet");
    }

    public void navigateToProductDetails(ProductDto product) {
        // getUI().ifPresent(ui -> ui.navigate("product" + productName));
        Notification.show(product.getProductName() + " Nevigating to Product Page is not Implemented yet");
    }


    public void clearSearchResults() {
        resultsDialog.removeAll();
        resultsDialog.setHeight(null);
        resultLayout.removeAll();
        shopLayoutsList.clear();
        shopToProductsMap.clear();
        shopLayoutMap.clear();
        productButtonMap.clear();
        isAnyProductFound = false;
        removeAll(); 
    }

}

