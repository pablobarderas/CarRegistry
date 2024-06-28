package com.bardev.CarRegistry.service.impl;

import com.bardev.CarRegistry.repository.BrandRepository;
import com.bardev.CarRegistry.repository.entity.BrandEntity;
import com.bardev.CarRegistry.repository.mapper.BrandEntityMapper;
import com.bardev.CarRegistry.service.model.Brand;
import com.bardev.CarRegistry.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandEntityMapper brandEntityMapper;

    @Override
    @Async
    public CompletableFuture<List<Brand>> getBrands() {

        // Find all brands
        List<Brand> brandList = brandEntityMapper.brandEntityListToBrandList
                (brandRepository.findAll());

        return CompletableFuture.completedFuture(brandList);
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
    @Async
    public CompletableFuture<List<Brand>> addBrands(List<Brand> brandList) {

        if (brandList.isEmpty()){
            return CompletableFuture.failedFuture(new Exception("Empty brand list"));
        }

        // Get brandsList
        try{
            List<BrandEntity> brandResponse =
                    brandRepository
                            .saveAll(brandEntityMapper
                                    .brandListToBrandEntityList(brandList));

            return CompletableFuture
                    .completedFuture(brandEntityMapper
                            .brandEntityListToBrandList(brandResponse) );
        }catch (Exception e){
            return CompletableFuture.failedFuture(e);
        }

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
