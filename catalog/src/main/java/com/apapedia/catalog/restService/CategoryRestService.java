package com.apapedia.catalog.restService;

import java.util.List;
import java.util.UUID;

import com.apapedia.catalog.model.Catalog;
import com.apapedia.catalog.model.Category;

public interface CategoryRestService {

    Category saveCategory(Category category);

    List<Category> getAllCategories();

    Category getCategoryById(UUID id);

    Category getCategoryBySlug(String slug);

    String categoryNameToSlug(String categoryName);

    List<Catalog> getCatalogByCategory(String slug);
}
