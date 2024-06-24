package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.service.model.Brand;

import java.util.List;

public interface IBrandService {
    List<Brand> getBrands();
    Brand getBrandById(Integer id);
    Brand addBrand(Brand brand);
    Brand updateBrand(Integer id, Brand brand);
    void deleteBrand(Integer id);
}
