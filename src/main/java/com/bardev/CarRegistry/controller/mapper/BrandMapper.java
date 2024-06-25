package com.bardev.CarRegistry.controller.mapper;

import com.bardev.CarRegistry.controller.dto.BrandDTO;
import com.bardev.CarRegistry.service.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "carEntityList", ignore = true)
    Brand brandDTOToBrand(BrandDTO brandDTO);

    BrandDTO brandToBrandDTO(Brand brand);

    List<BrandDTO> brandListToBrandDTOList(List<Brand> brandList);
    List<Brand> brandDTOListToBrandServiceList(List<BrandDTO> brandDTOList);

}
