package com.bardev.CarRegistry.repository.mapper;

import com.bardev.CarRegistry.repository.entity.BrandEntity;
import com.bardev.CarRegistry.service.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BrandEntityMapper {

    BrandEntityMapper mapper = Mappers.getMapper(BrandEntityMapper.class);

    // BrandEntity with carList and without it
    Brand brandEntityToBrand(BrandEntity brandEntity);

    BrandEntity brandToBrandEntity(Brand brand);

    List<BrandEntity> brandListToBrandEntityList(List<Brand> brandList);
    List<Brand> brandEntityListToBrandList(List<BrandEntity> brandEntityList);

}
