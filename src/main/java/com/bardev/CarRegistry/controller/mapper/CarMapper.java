package com.bardev.CarRegistry.controller.mapper;

import com.bardev.CarRegistry.controller.dto.BrandDTO;
import com.bardev.CarRegistry.controller.dto.CarDTO;
import com.bardev.CarRegistry.controller.dto.CarWithBrandDTO;
import com.bardev.CarRegistry.repository.entity.Brand;
import com.bardev.CarRegistry.service.model.BrandService;
import com.bardev.CarRegistry.service.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CarMapper {

    CarMapper mapper = Mappers.getMapper(CarMapper.class);

    @Mapping(target = "brand", source = "brandName", qualifiedByName = "stringToBrand")
    Car carDTOToCar(CarDTO carDTO);

    @Mapping(target = "brandName", source = "brand", qualifiedByName = "brandToString")
    CarDTO carToCarDTO(Car car);

    // Car with brand object
    CarWithBrandDTO carToCarWithBrandDTO(Car car);
    Car CarWithBrandDTOToCar(CarWithBrandDTO carWithBrandDTO);
    List<CarWithBrandDTO> carListToCarWithBrandDTOList(List<Car> carList);
    List<Car> carWithBrandDTOListToCarList(List<CarWithBrandDTO> carWithBrandDTOList);

    // Car without brandObject
    List<Car> carDTOListToCarList(List<CarDTO> carDTOList);
    List<CarDTO> carListToCarDTOList(List<Car> carList);

    // Custom mapping for Page
    default Page<Car> carDTOPageToCarPage(Page<CarDTO> carDTOPage) {
        List<Car> cars = carDTOPage.getContent().stream()
                .map(this::carDTOToCar)
                .collect(Collectors.toList());
        return new PageImpl<>(cars, carDTOPage.getPageable(), carDTOPage.getTotalElements());
    }

    default Page<CarDTO> carPageToCarDTOPage(Page<Car> carPage) {
        List<CarDTO> carDTOs = carPage.getContent().stream()
                .map(this::carToCarDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(carDTOs, carPage.getPageable(), carPage.getTotalElements());
    }
    // Brand with carList and without it
    @Mapping(target = "carEntityList", ignore = true)
    BrandService brandDTOToBrandService(BrandDTO brandDTO);
    BrandDTO brandServiceToBrandDTO(BrandService brandService);

    // Parse methods
    @Named("stringToBrand")
    default Brand stringToBrand(String brandName) {
        if (brandName == null) {
            return null;
        }
        Brand brand = new Brand();
        brand.setName(brandName);
        return brand;
    }

    @Named("brandToString")
    default String brandToString(Brand brand) {
        if (brand == null) {
            return null;
        }
        return brand.getName();
    }


}
