package com.apapedia.frontend.dto.response.catalog;

import java.util.List;

import com.apapedia.frontend.dto.request.catalog.CategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {

    private int status;
    private String message;
    private List<CategoryDTO> result;

}
