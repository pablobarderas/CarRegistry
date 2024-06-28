package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.service.model.Brand;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BrandService {
    CompletableFuture<List<Brand>> getBrands();
    CompletableFuture<List<Brand>> addBrands(List<Brand> brandList);
    Brand getBrandById(Integer id);
    Brand addBrand(Brand brand);
    Brand updateBrand(Integer id, Brand brand);
    void deleteBrand(Integer id);
}
