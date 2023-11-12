package com.apapedia.catalog;

import com.apapedia.catalog.model.Category;
import com.apapedia.catalog.restService.CategoryRestService;
import com.apapedia.catalog.restService.ListService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootApplication
public class CatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner run(CategoryRestService categoryRestService, ListService listService) {
		return args ->{
			try {
				for (String categoryName : listService.getCategoryList()) {
					Category category = new Category();
					category.setName(categoryName);
					categoryRestService.saveCategory(category);
				}
			} catch (DataIntegrityViolationException ignored) {}
		};
	}
}
