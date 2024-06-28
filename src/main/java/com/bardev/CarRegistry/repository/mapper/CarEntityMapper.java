package com.bardev.CarRegistry.repository.mapper;

import com.bardev.CarRegistry.controller.dto.CarWithBrandDTO;
import com.bardev.CarRegistry.repository.entity.CarEntity;
import com.bardev.CarRegistry.service.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CarEntityMapper {

    CarEntity carToCarEntity(Car car);
    Car carEntityToCar(CarEntity carEntity);
    List<CarEntity> carListToCarEntityList(List<Car> carList);
    List<Car> carEntityListToCarList(List<CarEntity> carEntityList);


    // Custom mapping for Page
    default Page<Car> carEntityPageToCarPage(Page<CarEntity> carEntityPage) {
        List<Car> cars = carEntityPage.getContent().stream()
                .map(this::carEntityToCar)
                .collect(Collectors.toList());
        return new PageImpl<>(cars, carEntityPage.getPageable(), carEntityPage.getTotalElements());
    }

    default Page<CarEntity> carPageToCarEntityPage(Page<Car> carPage) {
        List<CarEntity> carEntities = carPage.getContent().stream()
                .map(this::carToCarEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(carEntities, carPage.getPageable(), carPage.getTotalElements());
    }

}
