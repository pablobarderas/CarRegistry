package com.bardev.CarRegistry.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandService {

    private Integer id;
    private String name;
    private Integer warranty;
    private String country;
    private List<Car> carEntityList;

}
