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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    // ADD CAR
    @Override
    @Async
    public CompletableFuture<List<Car>> addCar(List<Car> carList) {

        // Update each car with brand
        List<Car> carListUpdated = carList.stream()
                .map(c->{
                 BrandEntity b =brandRepository
                                 .findByName(c.getBrand().getName())
                                 .orElseThrow(NoSuchElementException::new);
                 c.setBrand(b);
                 return c;
                }).toList();

        // Get completable future list
        CompletableFuture<List<CarEntity>> carEntityList = CompletableFuture.completedFuture
                (carRepository.saveAll
                        (carEntityMapper.carListToCarEntityList(carListUpdated)));

        return carEntityMapper.cfCarEntityListToCfCarList(carEntityList);
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
}
