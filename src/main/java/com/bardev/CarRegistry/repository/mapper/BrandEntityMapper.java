package com.bardev.CarRegistry.repository.mapper;

import com.bardev.CarRegistry.repository.entity.Brand;
import com.bardev.CarRegistry.service.model.BrandService;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BrandEntityMapper {

    BrandEntityMapper mapper = Mappers.getMapper(BrandEntityMapper.class);

    // Brand with carList and without it
    BrandService brandToBrandService(Brand brand);

    Brand brandServiceToBrand(BrandService brandService);

    List<Brand> brandServiceListToBrandList(List<BrandService> brandServiceList);
    List<BrandService> brandListToBrandServiceList(List<Brand> brandList);

}
