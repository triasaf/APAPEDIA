package com.apapedia.catalog;

import com.apapedia.catalog.dto.CatalogMapper;
import com.apapedia.catalog.dto.request.SubstractCatalogStockDTO;
import com.apapedia.catalog.dto.request.UpdateCatalogRequestDTO;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.restService.CatalogRestServiceImpl;
import com.apapedia.catalog.restService.CategoryRestService;
import com.apapedia.catalog.repository.CatalogDb;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CatalogRestServiceImplTest {

    @Autowired
    private CatalogRestServiceImpl catalogRestService;

    @MockBean
    private CategoryRestService categoryRestService;

    @MockBean
    private CatalogDb catalogDb;

    @MockBean
    private CatalogMapper catalogMapper;

    @Test
    public void getAllCatalogsSortedByNameTest() {
        Catalog catalog1 = new Catalog();
        catalog1.setProductName("Product A");
        Catalog catalog2 = new Catalog();
        catalog2.setProductName("Product B");
        List<Catalog> expectedCatalogs = Arrays.asList(catalog1, catalog2);

        when(catalogDb.findAllByIsDeletedFalseOrderByProductName()).thenReturn(expectedCatalogs);

        List<Catalog> actualCatalogs = catalogRestService.getAllCatalogsSortedByName();

        assertEquals(expectedCatalogs, actualCatalogs);
    }

    @Test
    public void getCatalogByIdTest() {
        UUID id = UUID.randomUUID();
        Catalog expectedCatalog = new Catalog();
        expectedCatalog.setProductName("Product A");

        when(catalogDb.findById(id)).thenReturn(Optional.of(expectedCatalog));

        Catalog actualCatalog = catalogRestService.getCatalogById(id);

        assertEquals(expectedCatalog, actualCatalog);
    }

    @Test
    public void getCatalogByIdNotFoundTest() {
        UUID id = UUID.randomUUID();

        when(catalogDb.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> catalogRestService.getCatalogById(id));
    }

    @Test
    public void updateCatalogTest() {
        UUID id = UUID.randomUUID();
        Catalog existingCatalog = new Catalog();
        existingCatalog.setProductName("Product A");
        existingCatalog.setPrice(100);
        existingCatalog.setProductDescription("Description A");
        existingCatalog.setStok(10);
        existingCatalog.setCategoryId(new Category());

        UpdateCatalogRequestDTO catalogDTO = new UpdateCatalogRequestDTO();
        catalogDTO.setId(id);
        catalogDTO.setProductName("Product B");
        catalogDTO.setPrice(200);
        catalogDTO.setProductDescription("Description B");
        catalogDTO.setStok(20);
        Category category = new Category();
        category.setId(UUID.randomUUID()); // Set a valid Category ID
        catalogDTO.setCategoryId(category);

        Catalog updatedCatalog = new Catalog();
        updatedCatalog.setProductName(catalogDTO.getProductName());
        updatedCatalog.setPrice(catalogDTO.getPrice());
        updatedCatalog.setProductDescription(catalogDTO.getProductDescription());
        updatedCatalog.setStok(catalogDTO.getStok());
        updatedCatalog.setCategoryId(new Category());

        when(catalogDb.findById(id)).thenReturn(Optional.of(existingCatalog));
        when(catalogMapper.updateCatalogRequestDTOToCatalog(catalogDTO)).thenReturn(updatedCatalog);
        when(catalogDb.save(existingCatalog)).thenReturn(existingCatalog);

        Catalog actualCatalog = catalogRestService.updateCatalog(catalogDTO);

        assertEquals(updatedCatalog.getProductName(), actualCatalog.getProductName());
        assertEquals(updatedCatalog.getPrice(), actualCatalog.getPrice());
        assertEquals(updatedCatalog.getProductDescription(), actualCatalog.getProductDescription());
        assertEquals(updatedCatalog.getStok(), actualCatalog.getStok());
        assertEquals(updatedCatalog.getCategoryId(), actualCatalog.getCategoryId());
        verify(catalogDb, times(1)).save(existingCatalog);
    }

    @Test
    public void getCatalogListByPriceRangeTest() {
        Catalog catalog1 = new Catalog();
        catalog1.setPrice(100);
        Catalog catalog2 = new Catalog();
        catalog2.setPrice(200);
        List<Catalog> expectedCatalogs = Arrays.asList(catalog1, catalog2);

        when(catalogDb.findByPriceBetweenAndIsDeletedFalse(100, 200)).thenReturn(expectedCatalogs);

        List<Catalog> actualCatalogs = catalogRestService.getCatalogListByPriceRange(100, 200);

        assertEquals(expectedCatalogs, actualCatalogs);
    }

    @Test
    public void getCatalogListByPriceRangeNotFoundTest() {
        when(catalogDb.findByPriceBetweenAndIsDeletedFalse(100, 200)).thenReturn(Arrays.asList());

        assertThrows(NoSuchElementException.class, () -> catalogRestService.getCatalogListByPriceRange(100, 200));
    }

    @Test
    public void getCatalogListByFilterTest() {
        Catalog catalog1 = new Catalog();
        catalog1.setPrice(100);
        Catalog catalog2 = new Catalog();
        catalog2.setPrice(200);
        List<Catalog> expectedCatalogs = Arrays.asList(catalog1, catalog2);

        when(catalogDb.findByPriceBetweenAndIsDeletedFalse(100, 200)).thenReturn(expectedCatalogs);
        when(categoryRestService.getCatalogByCategory("category")).thenReturn(expectedCatalogs);

        List<Catalog> actualCatalogs = catalogRestService.getCatalogListByFilter(100, 200, "category");

        assertEquals(expectedCatalogs, actualCatalogs);
    }

    @Test
    public void getCatalogListByFilterNotFoundTest() {
        when(catalogDb.findByPriceBetweenAndIsDeletedFalse(100, 200)).thenReturn(Arrays.asList());
        when(categoryRestService.getCatalogByCategory("category")).thenReturn(Arrays.asList());

        assertThrows(NoSuchElementException.class,
                () -> catalogRestService.getCatalogListByFilter(100, 200, "category"));
    }

    @Test
    public void getCatalogListByFilterInvalidParametersTest() {
        assertThrows(IllegalArgumentException.class, () -> catalogRestService.getCatalogListByFilter(null, null, null));
    }

    @Test
    public void deleteCatalogTest() {
        UUID id = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        Catalog catalog = new Catalog();
        catalog.setSeller(sellerId);

        when(catalogDb.findById(id)).thenReturn(Optional.of(catalog));

        catalogRestService.deleteCatalog(id, sellerId);

        verify(catalogDb, times(1)).save(catalog);
    }

    @Test
    public void deleteCatalogNotAuthorizedTest() {
        UUID id = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        Catalog catalog = new Catalog();
        catalog.setSeller(UUID.randomUUID());

        when(catalogDb.findById(id)).thenReturn(Optional.of(catalog));

        assertThrows(BadCredentialsException.class, () -> catalogRestService.deleteCatalog(id, sellerId));
    }

    @Test
    public void substractCatalogStockTest() {
        UUID id = UUID.randomUUID();
        Catalog catalog = new Catalog();
        catalog.setStok(10);

        SubstractCatalogStockDTO stockDTO = new SubstractCatalogStockDTO();
        stockDTO.setCatalogId(id);
        stockDTO.setStockReduced(5);

        when(catalogDb.findById(id)).thenReturn(Optional.of(catalog));

        catalogRestService.substractCatalogStock(stockDTO);

        assertEquals(5, catalog.getStok());
        verify(catalogDb, times(1)).save(catalog);
    }

    @Test
    public void createCatalogTest() {
        Catalog catalog = new Catalog();
        catalog.setProductName("Product A");

        when(catalogDb.save(catalog)).thenReturn(catalog);

        Catalog createdCatalog = catalogRestService.createCatalog(catalog);

        assertEquals(catalog, createdCatalog);
    }

    @Test
    public void getCatalogsBySellerIdTest() {
        UUID sellerId = UUID.randomUUID();
        Catalog catalog1 = new Catalog();
        catalog1.setProductName("Product A");
        Catalog catalog2 = new Catalog();
        catalog2.setProductName("Product B");
        List<Catalog> expectedCatalogs = Arrays.asList(catalog1, catalog2);

        when(catalogDb.findAllBySellerAndIsDeletedFalseOrderByProductName(sellerId)).thenReturn(expectedCatalogs);

        List<Catalog> actualCatalogs = catalogRestService.getCatalogsBySellerId(sellerId, null, null, null);

        assertEquals(expectedCatalogs, actualCatalogs);
    }

    @Test
    public void getCatalogListByProductNameTest() {
        String productName = "Product";
        UUID sellerId = UUID.randomUUID();
        Catalog catalog1 = new Catalog();
        catalog1.setProductName("Product A");
        Catalog catalog2 = new Catalog();
        catalog2.setProductName("Product B");
        List<Catalog> expectedCatalogs = Arrays.asList(catalog1, catalog2);

        when(catalogDb
                .findAllByProductNameContainingIgnoreCaseAndSellerAndIsDeletedFalseOrderByProductName(productName, sellerId))
                .thenReturn(expectedCatalogs);

        List<Catalog> actualCatalogs = catalogRestService.getCatalogListByProductName(productName, sellerId);

        assertEquals(expectedCatalogs, actualCatalogs);
    }

    @Test
    public void getSortedCatalogTest() {
        UUID sellerId = UUID.randomUUID();
        Catalog catalog1 = new Catalog();
        catalog1.setProductName("Product A");
        Catalog catalog2 = new Catalog();
        catalog2.setProductName("Product B");
        List<Catalog> expectedCatalogs = Arrays.asList(catalog1, catalog2);

        when(catalogDb.findAllBySellerAndIsDeletedFalseOrderByProductName(sellerId)).thenReturn(expectedCatalogs);

        List<Catalog> actualCatalogs = catalogRestService.getSortedCatalog("name", "asc", sellerId);

        assertEquals(expectedCatalogs, actualCatalogs);
    }

}
