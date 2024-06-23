package com.bardev.CarRegistry.controller.mapper;

import com.bardev.CarRegistry.controller.dto.BrandDTO;
import com.bardev.CarRegistry.repository.entity.Brand;
import com.bardev.CarRegistry.service.model.BrandService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BrandMapper {

    BrandMapper mapper = Mappers.getMapper(BrandMapper.class);

    @Mapping(target = "carEntityList", ignore = true)
    BrandService brandDTOToBrandService(BrandDTO brandDTO);

    BrandDTO brandServiceToBrandDTO(BrandService brandService);

    List<BrandDTO> brandServiceListToBrandDTOList(List<BrandService> brandServiceList);
    List<BrandService> brandDTOListToBrandServiceList(List<BrandDTO> brandDTOList);

}
