package com.appsdeveloperblog.ws.products.rest;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRestModel {

    private String title;
    private BigDecimal price;
    private Integer quantity;
}
