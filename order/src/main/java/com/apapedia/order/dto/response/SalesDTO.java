package com.apapedia.order.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesDTO {
    private Date date;
    private int numberOfProductsSold;
}
