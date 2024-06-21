package com.bardev.CarRegistry.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandService {

    Integer id;
    private String name;
    private Integer warranty;
    private String country;
    List<Car> carEntityList;

}
