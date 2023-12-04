package com.apapedia.frontend.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesResponseDTO {
    private Date date;
    private int numberOfProductsSold;
}
