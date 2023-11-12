package com.apapedia.catalog.restService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.apapedia.catalog.model.Catalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.repository.CatalogDb;
import com.apapedia.catalog.repository.CategoryDb;

@Service
public class CategoryRestServiceImpl implements CategoryRestService {

    @Autowired
    private CatalogDb catalogDb;

    @Autowired
    private CategoryDb categoryDb;

    @Override
    public Category saveCategory(Category category) {
        return categoryDb.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDb.findAll();
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryDb.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        Category categoryFound = null;
        for (Category category : getAllCategories()) {
            if (categoryNameToSlug(category.getName()).equals(slug)) {
                categoryFound = category;
            }
        }

        if (categoryFound != null) return categoryFound;
        else throw new NoSuchElementException("Category not found");
    }

    @Override
    public String categoryNameToSlug(String categoryName) {
        return categoryName.replaceAll(" ", "-").replaceAll("&", "n").toLowerCase();
    }

    @Override
    public List<Catalog> getCatalogByCategory(String slug) {
        var category = getCategoryBySlug(slug);

        return category.getListCatalog();
    }
}
