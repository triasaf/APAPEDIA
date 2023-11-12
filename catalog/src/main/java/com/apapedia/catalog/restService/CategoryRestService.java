package com.apapedia.catalog.restService;

import java.util.List;
import java.util.UUID;

import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;

public interface CategoryRestService {

    public Category saveCategory(Category category);

    public List<Category> getAllCategories();

    public Category getCategoryById(UUID id);

    public Category getCategoryBySlug(String slug);

    public String categoryNameToSlug(String categoryName);

    public List<Catalog> getCatalogByCategory(String slug);
}
