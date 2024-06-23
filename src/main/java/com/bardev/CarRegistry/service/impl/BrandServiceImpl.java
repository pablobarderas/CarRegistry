package com.bardev.CarRegistry.service.impl;

import com.bardev.CarRegistry.repository.BrandRepository;
import com.bardev.CarRegistry.repository.mapper.BrandEntityMapper;
import com.bardev.CarRegistry.service.model.BrandService;
import com.bardev.CarRegistry.service.IBrandService;
import com.bardev.CarRegistry.service.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    BrandRepository brandRepository;

    @Override
    public List<BrandService> getBrands() {
        return BrandEntityMapper.mapper.brandListToBrandServiceList(brandRepository.findAll());
    }

    @Override
    public BrandService getBrandById(Integer id) {
        return brandRepository
                .findById(id)
                .map(BrandEntityMapper.mapper::brandToBrandService)
                .orElseThrow(NoSuchFieldError::new);
    }

    @Override
    public BrandService addBrand(BrandService brandService) {

        if (brandService == null){
            throw new IllegalArgumentException();
        }

        return BrandEntityMapper
                .mapper
                .brandToBrandService(
                brandRepository
                        .save(BrandEntityMapper.mapper.brandServiceToBrand(brandService)));
    }

    @Override
    public BrandService updateBrand(Integer id, BrandService brandService) {

        // Check exist
        if (!brandRepository.existsById(id)){
            throw new NoSuchElementException("The car is not present on database");
        }

        // Set id and save brand
        BrandService brandServiceUpdate = brandService;
        brandServiceUpdate.setId(id);

        return BrandEntityMapper.mapper.brandToBrandService(
                brandRepository
                        .save(BrandEntityMapper.mapper
                                .brandServiceToBrand(brandServiceUpdate)));

    }

    @Override
    public void deleteBrand(Integer id) {

        // Check exist
        if (!brandRepository.existsById(id)){
            throw new NoSuchElementException("The car is not present on database");
        }
        brandRepository.deleteById(id);

    }
}
