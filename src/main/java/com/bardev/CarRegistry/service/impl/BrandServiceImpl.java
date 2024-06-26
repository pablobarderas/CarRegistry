package com.bardev.CarRegistry.service.impl;

import com.bardev.CarRegistry.repository.BrandRepository;
import com.bardev.CarRegistry.repository.mapper.BrandEntityMapper;
import com.bardev.CarRegistry.service.model.Brand;
import com.bardev.CarRegistry.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandEntityMapper brandEntityMapper;

    @Override
    public List<Brand> getBrands() {
        return brandEntityMapper.brandEntityListToBrandList(brandRepository.findAll());
    }

    @Override
    public Brand getBrandById(Integer id) {
        return brandRepository
                .findById(id)
                .map(brandEntityMapper::brandEntityToBrand)
                .orElseThrow(NoSuchFieldError::new);
    }

    @Override
    public Brand addBrand(Brand brand) {

        if (brand == null){
            throw new IllegalArgumentException();
        }

        return brandEntityMapper
                .brandEntityToBrand(
                brandRepository
                        .save(brandEntityMapper.brandToBrandEntity(brand)));
    }

    @Override
    public Brand updateBrand(Integer id, Brand brand) {

        // Check exist
        if (!brandRepository.existsById(id)){
            throw new NoSuchElementException("The car is not present on database");
        }

        // Set id and save brandEntity
        Brand brandUpdate = brand;
        brandUpdate.setId(id);

        return brandEntityMapper.brandEntityToBrand(
                brandRepository
                        .save(brandEntityMapper
                                .brandToBrandEntity(brandUpdate)));

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
