package DmainTests;

import org.junit.jupiter.api.*;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import Domain.ShoppingBasket;
import Domain.Discounts.FixedDiscount;
import Domain.Discounts.PrecentageDiscount;

public class DiscountTest {
    // Precentage Discount tests
    @Mock
    private ShoppingBasket _basketMock;

    @BeforeEach
    public void setUp() {
        _basketMock = mock(ShoppingBasket.class);
    }

    @Test
    public void testApplyPrecentageDiscountLogic_whenProductNotInBasket_thenNoDiscountApplied() {
        // Arrange
        PrecentageDiscount discount = new PrecentageDiscount(new Date(), 10, 1);
        when(_basketMock.getProductCount(1)).thenReturn(0);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        verify(_basketMock, never()).getProductPriceToAmount(anyInt());
    }

    @Test
    public void testApplyPrecentageDiscountLogic_whenProductInBasket_thenPriceAndAmountUpdated() {
        // Arrange
        PrecentageDiscount discount = new PrecentageDiscount(new Date(), 10, 1);
        when(_basketMock.getProductCount(1)).thenReturn(1);
        SortedMap<Double, Integer> priceToAmount = new TreeMap<>();
        priceToAmount.put(10.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(1)).thenReturn(priceToAmount);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        assertFalse(priceToAmount.containsKey(10.0)); // the old price was removed
        assertTrue(priceToAmount.containsKey(9.0)); // the new price was added
        assertEquals(1, priceToAmount.get(9.0)); // the price was updated
    }

    @Test
    public void testApplyPrecentageDiscountLogic_whenMultipleProductsInBasket_thenDiscountAppliedToMostExpensive() {
        // Arrange
        PrecentageDiscount discount = new PrecentageDiscount(new Date(), 10, 1);
        when(_basketMock.getProductCount(1)).thenReturn(2);
        SortedMap<Double, Integer> priceToAmount = new TreeMap<>();
        priceToAmount.put(10.0, 1); // price, amount
        priceToAmount.put(20.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(1)).thenReturn(priceToAmount);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        assertFalse(priceToAmount.containsKey(20.0)); // the old highest price was removed
        assertTrue(priceToAmount.containsKey(18.0)); // the new price was added
        assertEquals(1, priceToAmount.get(18.0)); // the price was updated
    }

    @Test
    public void testApplyPrecentageDiscountLogic_whenDifferentProductInBasket_thenNoDiscountApplied() {
        // Arrange
        PrecentageDiscount discount = new PrecentageDiscount(new Date(), 10, 1);
        when(_basketMock.getProductCount(1)).thenReturn(0);
        when(_basketMock.getProductCount(2)).thenReturn(1);
        SortedMap<Double, Integer> priceToAmount = new TreeMap<>();
        priceToAmount.put(10.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(2)).thenReturn(priceToAmount);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        verify(_basketMock, never()).getProductPriceToAmount(1);
    }

    @Test
    public void testApplyPrecentageDiscountLogic_whenMultipleProductsInBasket_thenDiscountAppliedToCorrectProduct() {
        // Arrange
        PrecentageDiscount discount = new PrecentageDiscount(new Date(), 10, 1);
        when(_basketMock.getProductCount(1)).thenReturn(1);
        when(_basketMock.getProductCount(2)).thenReturn(1);
        SortedMap<Double, Integer> priceToAmount1 = new TreeMap<>();
        priceToAmount1.put(10.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(1)).thenReturn(priceToAmount1);
        SortedMap<Double, Integer> priceToAmount2 = new TreeMap<>();
        priceToAmount2.put(20.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(2)).thenReturn(priceToAmount2);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        assertFalse(priceToAmount1.containsKey(10.0)); // the old price was removed
        assertTrue(priceToAmount1.containsKey(9.0)); // the new price was added
        assertEquals(1, priceToAmount1.get(9.0)); // the price was updated
        assertTrue(priceToAmount2.containsKey(20.0)); // the other product was not updated
    }

    // Fixed Discount tests
    @Test
    public void testApplyFixedDiscountLogic_whenProductNotInBasket_thenNoDiscountApplied() {
        // Arrange
        FixedDiscount discount = new FixedDiscount(new Date(), 1, 1);
        when(_basketMock.getProductCount(1)).thenReturn(0);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        verify(_basketMock, never()).getProductPriceToAmount(anyInt());
    }

    @Test
    public void testApplyFixedDiscountLogic_whenProductInBasket_thenPriceAndAmountUpdated() {
        // Arrange
        FixedDiscount discount = new FixedDiscount(new Date(), 1, 1);
        when(_basketMock.getProductCount(1)).thenReturn(1);
        SortedMap<Double, Integer> priceToAmount = new TreeMap<>();
        priceToAmount.put(10.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(1)).thenReturn(priceToAmount);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        assertFalse(priceToAmount.containsKey(10.0)); // the old price was removed
        assertTrue(priceToAmount.containsKey(9.0)); // the new price was added
        assertEquals(1, priceToAmount.get(9.0)); // the price was updated
    }

    @Test
    public void testApplyFixedDiscountLogic_whenMultipleProductsInBasket_thenDiscountAppliedToMostExpensive() {
        // Arrange
        FixedDiscount discount = new FixedDiscount(new Date(), 2, 1);
        when(_basketMock.getProductCount(1)).thenReturn(2);
        SortedMap<Double, Integer> priceToAmount = new TreeMap<>();
        priceToAmount.put(10.0, 1); // price, amount
        priceToAmount.put(20.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(1)).thenReturn(priceToAmount);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        assertFalse(priceToAmount.containsKey(20.0)); // the old highest price was removed
        assertTrue(priceToAmount.containsKey(18.0)); // the new price was added
        assertEquals(1, priceToAmount.get(18.0)); // the price was updated
    }

    @Test
    public void testApplyFixedDiscountLogic_whenDifferentProductInBasket_thenNoDiscountApplied() {
        // Arrange
        FixedDiscount discount = new FixedDiscount(new Date(), 1, 1);
        when(_basketMock.getProductCount(1)).thenReturn(0);
        when(_basketMock.getProductCount(2)).thenReturn(1);
        SortedMap<Double, Integer> priceToAmount = new TreeMap<>();
        priceToAmount.put(10.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(2)).thenReturn(priceToAmount);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        verify(_basketMock, never()).getProductPriceToAmount(1);
    }

    @Test
    public void testApplyFixedDiscountLogic_whenMultipleProductsInBasket_thenDiscountAppliedToCorrectProduct() {
        // Arrange
        FixedDiscount discount = new FixedDiscount(new Date(), 1, 1);
        when(_basketMock.getProductCount(1)).thenReturn(1);
        when(_basketMock.getProductCount(2)).thenReturn(1);
        SortedMap<Double, Integer> priceToAmount1 = new TreeMap<>();
        priceToAmount1.put(10.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(1)).thenReturn(priceToAmount1);
        SortedMap<Double, Integer> priceToAmount2 = new TreeMap<>();
        priceToAmount2.put(20.0, 1); // price, amount
        when(_basketMock.getProductPriceToAmount(2)).thenReturn(priceToAmount2);

        // Act
        discount.applyDiscountLogic(_basketMock);

        // Assert
        assertFalse(priceToAmount1.containsKey(10.0)); // the old price was removed
        assertTrue(priceToAmount1.containsKey(9.0)); // the new price was added
        assertEquals(1, priceToAmount1.get(9.0)); // the price was updated
        assertTrue(priceToAmount2.containsKey(20.0)); // the other product was not updated
    }

}