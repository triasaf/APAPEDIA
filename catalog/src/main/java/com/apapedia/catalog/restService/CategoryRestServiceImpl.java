package com.apapedia.catalog.restService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.repository.CatalogDb;
import com.apapedia.catalog.repository.CategoryDb;

import java.util.List;

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

}
