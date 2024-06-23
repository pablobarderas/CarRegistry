package com.bardev.CarRegistry.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {
    Integer id;
    private String name;
    private Integer warranty;
    private String country;
}
