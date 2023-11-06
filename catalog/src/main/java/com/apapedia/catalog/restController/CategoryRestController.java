package com.apapedia.catalog.restController;

import com.apapedia.catalog.restService.CatalogRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
public class CategoryRestController {
    @Autowired
    private CatalogRestService catalogRestService;
}
