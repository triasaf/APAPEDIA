package com.apapedia.catalog.restService;

import java.util.List;
import java.util.UUID;

import org.glassfish.jaxb.core.annotation.OverrideAnnotationOf;
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
    public List<Category> getAllCategories() {
        return categoryDb.findAll();
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryDb.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

}
