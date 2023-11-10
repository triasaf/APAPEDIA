package com.apapedia.catalog.restService;

import java.util.List;
import java.util.UUID;

import com.apapedia.catalog.model.Category;

public interface CategoryRestService {

    List<Category> getAllCategories();

    Category getCategoryById(UUID id);

}
