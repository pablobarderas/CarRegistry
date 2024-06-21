package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.service.model.BrandService;

import java.util.List;

public interface IBrandService {
    List<BrandService> getBrands();
    BrandService getBrandById(Integer id);
    BrandService addBrand(BrandService brandService);
    BrandService updateBrand(Integer id, BrandService brandService);
    void deleteBrand(Integer id);
}
