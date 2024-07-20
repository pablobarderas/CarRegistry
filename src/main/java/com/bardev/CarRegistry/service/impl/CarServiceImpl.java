package com.bardev.CarRegistry.service.impl;

import com.bardev.CarRegistry.repository.BrandRepository;
import com.bardev.CarRegistry.repository.CarRepository;
import com.bardev.CarRegistry.repository.entity.BrandEntity;
import com.bardev.CarRegistry.repository.entity.CarEntity;
import com.bardev.CarRegistry.repository.mapper.BrandEntityMapper;
import com.bardev.CarRegistry.repository.mapper.CarEntityMapper;
import com.bardev.CarRegistry.service.CarService;
import com.bardev.CarRegistry.service.model.Brand;
import com.bardev.CarRegistry.service.model.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CarEntityMapper carEntityMapper;

    @Autowired
    private BrandEntityMapper brandEntityMapper;

    private final String[] HEADERS = {"ID", "BRAND", "MODEL", "MILEAGE", "PRICE", "YEAR", "DESCRIPTION",
            "COLOUR", "FUEL_TYPE", "NUM_DOORS"};


    // GET ALL CARS
    // If you have no cars, return empty list
    @Override
    @Async
    public CompletableFuture<List<Car>> getCars() {

        // Find all cars
        List<Car> carList = carEntityMapper.carEntityListToCarList
                (carRepository.findAll());

        return CompletableFuture.completedFuture(carList);
    }

    // GET CAR BY ID
    // If car not exist, throw new NoSuchElementException
    @Override
    public Car getCarById(Integer id) {
        return  carRepository.findById(id)
                .map(carEntityMapper::carEntityToCar)
                .orElseThrow(() -> new NoSuchElementException("Car with ID " + id + " not found"));
    }

    // ADD CARS
    @Override
    @Async
    public CompletableFuture<List<Car>> addCars(List<Car> carList) {

        // Get all brands with one call
        List<BrandEntity> brandsList = brandRepository.findAll();

        // Update each car with brand, filter brands with car brand name and return it
        List<Car> carListUpdated = carList.stream()
                .peek(car -> car.setBrand(
                        brandsList.stream()
                                .filter(
                                b-> b.getName().equals(car.getBrand().getName())
                        ).findFirst().orElseThrow(NoSuchElementException::new)
                ))
                .toList();


        // Add and get car entities list
        List<CarEntity> carEntitiesList =
                carRepository
                .saveAll(carEntityMapper.carListToCarEntityList(carListUpdated));

        return CompletableFuture.completedFuture(carEntityMapper.carEntityListToCarList(carEntitiesList));
    }

    // UPDATE CAR
    // If car not exist, throw new NoSuchElementException
    @Override
    public Car updateCar(Integer id, Car car) {

        if (!carRepository.existsById(id)){
            throw new NoSuchElementException("The car is not present on database");
        }

        // Search brandEntity
        BrandEntity brand = brandRepository.findByName(car.getBrand().getName())
                .orElseThrow(NoSuchElementException::new);

        // Update car
        CarEntity entity = carEntityMapper.carToCarEntity(car);

        // Set id and brandEntity of car
        entity.setId(car.getId());
        entity.setBrand(brand);

        return carEntityMapper.carEntityToCar(
                carRepository.save(entity));
    }

    // ADD CAR
    @Override
    public Car addCar(Car car) {

        // Check car is not null
        Optional.ofNullable(car)
                .orElseThrow(() -> new IllegalArgumentException("The car is not present in the request"));

        // Check brand and brand name not null
        Optional.ofNullable(car.getBrand())
                .map(BrandEntity::getName)
                .orElseThrow(() -> new IllegalArgumentException("The car brand is not present or incomplete"));

        // Search and get brand
        BrandEntity brand = brandRepository.findByName(car.getBrand().getName())
                .orElseThrow(() -> new NoSuchElementException("Brand not found: " + car.getBrand().getName()));

        // Map car to entity
        CarEntity entity = carEntityMapper.carToCarEntity(car);

        // Set brandEntity of car
        entity.setBrand(brand);

        return carEntityMapper.carEntityToCar(
                carRepository.save(entity));
    }

    // DELETE CAR
    @Override
    public void deleteCar(Integer id) {
        if (!carRepository.existsById(id)){
            throw new NoSuchElementException("The car is not present on database");
        }
        carRepository.deleteById(id);
    }

    @Override
    public Page<Car> findAllPageable(Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Car> pageCar = carEntityMapper.carEntityPageToCarPage
                (carRepository.findAll(pageRequest));

        return pageCar;
    }

    @Override
    public String getCarsCsv() {
        List<CarEntity> carsList = carRepository.findAll();

        // Fill csv with cars
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(Arrays.toString(HEADERS)).append("\n");

        for (CarEntity car: carsList){
            csvContent
                    .append(car.getId()).append(",")
                    .append(car.getBrand().getName()).append(",")
                    .append(car.getModel()).append(",")
                    .append(car.getMileage()).append(",")
                    .append(car.getPrice()).append(",")
                    .append(car.getYear()).append(",")
                    .append(car.getDescription()).append(",")
                    .append(car.getColour()).append(",")
                    .append(car.getFuelType()).append(",")
                    .append(car.getNumDoors()).append("\n");

        }
        return csvContent.toString();
    }
}
