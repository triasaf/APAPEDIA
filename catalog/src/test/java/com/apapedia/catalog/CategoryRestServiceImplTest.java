package com.apapedia.catalog;

import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.restService.CategoryRestServiceImpl;
import com.apapedia.catalog.repository.CategoryDb;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CategoryRestServiceImplTest {

    @Autowired
    private CategoryRestServiceImpl categoryRestService;

    @MockBean
    private CategoryDb categoryDb;

    @Test
    public void saveCategoryTest() {
        Category category = new Category();
        category.setName("Category A");

        when(categoryDb.save(category)).thenReturn(category);

        Category savedCategory = categoryRestService.saveCategory(category);

        assertEquals(category, savedCategory);
        verify(categoryDb, times(1)).save(category);
    }

    @Test
    public void getAllCategoriesTest() {
        Category category1 = new Category();
        category1.setName("Category A");
        Category category2 = new Category();
        category2.setName("Category B");
        List<Category> expectedCategories = Arrays.asList(category1, category2);

        when(categoryDb.findAll()).thenReturn(expectedCategories);

        List<Category> actualCategories = categoryRestService.getAllCategories();

        assertEquals(expectedCategories, actualCategories);
    }

    @Test
    public void getCategoryByIdTest() {
        UUID id = UUID.randomUUID();
        Category expectedCategory = new Category();
        expectedCategory.setName("Category A");

        when(categoryDb.findById(id)).thenReturn(Optional.of(expectedCategory));

        Category actualCategory = categoryRestService.getCategoryById(id);

        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    public void getCategoryByIdNotFoundTest() {
        UUID id = UUID.randomUUID();

        when(categoryDb.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryRestService.getCategoryById(id));
    }

    @Test
    public void getCategoryBySlugTest() {
        Category category1 = new Category();
        category1.setName("Category A");
        Category category2 = new Category();
        category2.setName("Category B");
        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryDb.findAll()).thenReturn(categories);

        Category actualCategory = categoryRestService.getCategoryBySlug("category-a");

        assertEquals(category1, actualCategory);
    }

    @Test
    public void getCategoryBySlugNotFoundTest() {
        Category category1 = new Category();
        category1.setName("Category A");
        Category category2 = new Category();
        category2.setName("Category B");
        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryDb.findAll()).thenReturn(categories);

        assertThrows(NoSuchElementException.class, () -> categoryRestService.getCategoryBySlug("category-c"));
    }

    @Test
    public void categoryNameToSlugTest() {
        String categoryName = "Category & Name";
        String expectedSlug = "category-n-name";

        String actualSlug = categoryRestService.categoryNameToSlug(categoryName);

        assertEquals(expectedSlug, actualSlug);
    }

    @Test
    public void getCatalogByCategoryTest() {
        String slug = "category-a";
        Category category = new Category();
        category.setName("Category A");
        Catalog catalog1 = new Catalog();
        catalog1.setProductName("Product A");
        Catalog catalog2 = new Catalog();
        catalog2.setProductName("Product B");
        List<Catalog> expectedCatalogs = Arrays.asList(catalog1, catalog2);
        category.setListCatalog(expectedCatalogs);

        when(categoryDb.findAll()).thenReturn(Arrays.asList(category));

        List<Catalog> actualCatalogs = categoryRestService.getCatalogByCategory(slug);

        assertEquals(expectedCatalogs, actualCatalogs);
    }
}
