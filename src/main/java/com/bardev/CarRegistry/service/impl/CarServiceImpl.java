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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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
    @Override
    public List<Car> getCars() {
        return carEntityMapper.carEntityListToCarList(carRepository.findAll());
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
    public Car addCar(Car car) {

        if (car==null){
            throw new IllegalArgumentException();
        }

        // Get brand from Car
        Brand brand =  brandRepository.findByName(
                car.getBrand().getName())
                .map(brandEntityMapper::brandEntityToBrand)
                .orElseThrow(() -> new NoSuchElementException("BrandEntity not found with id: " + car.getBrand().getId()));

        // Set brandEntity
        CarEntity entity = carEntityMapper.carToCarEntity(car);
        entity.setBrand(brandEntityMapper.brandToBrandEntity(brand));

        return  carEntityMapper.carEntityToCar
                (carRepository.save(entity));
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
