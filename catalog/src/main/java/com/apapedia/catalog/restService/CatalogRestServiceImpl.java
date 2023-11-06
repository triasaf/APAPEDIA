package com.apapedia.catalog.restService;

import com.apapedia.catalog.repository.CatalogDb;
import com.apapedia.catalog.repository.CategoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatalogRestServiceImpl implements CatalogRestService{
    @Autowired
    private CatalogDb catalogDb;

    @Autowired
    private CategoryDb categoryDb;
}
