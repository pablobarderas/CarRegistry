package com.bardev.CarRegistry.service.impl;

import com.bardev.CarRegistry.repository.BrandRepository;
import com.bardev.CarRegistry.repository.CarRepository;
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


    // GET ALL CARS
    @Override
    public List<Car> getCars() {
        return CarEntityMapper.mapper.carEntityListToCarList(carRepository.findAll());
    }

    // GET CAR BY ID
    // If car not exist, throw new NoSuchElementException
    @Override
    public Car getCarById(Integer id) {
        return  carRepository.findById(id)
                .map(CarEntityMapper.mapper::carEntityToCar)
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
                .map(BrandEntityMapper.mapper::brandEntityToBrand)
                .orElseThrow(() -> new NoSuchElementException("BrandEntity not found with id: " + car.getBrand().getId()));

        // Set brandEntity
        Car carCorrect = car;
        carCorrect.setBrand(BrandEntityMapper.mapper.brandToBrandEntity(brand));

        log.info(carCorrect.getBrand().getName());

        return  CarEntityMapper.mapper.carEntityToCar
                (carRepository.save(
                        CarEntityMapper.mapper.carToCarEntity(carCorrect)));
    }

    // UPDATE CAR
    // If car not exist, throw new NoSuchElementException
    @Override
    public Car updateCar(Integer id, Car car) {

        if (!carRepository.existsById(id)){
            throw new NoSuchElementException("The car is not present on database");
        }

        // probar a hacerlo directamente con el car

        // Search brandEntity
        Brand brand = brandRepository.findByName(String.valueOf(car.getBrand().getName()))
                .map(BrandEntityMapper.mapper::brandEntityToBrand)
                .orElseThrow(NoSuchElementException::new);

        // Search car
        Car carUpdate =
                carRepository.findById(id)
                .map(CarEntityMapper.mapper::carEntityToCar)
                .orElseThrow(NoSuchElementException::new);



        // Set id and brandEntity of car
        carUpdate.setId(car.getId());
        carUpdate.setBrand(BrandEntityMapper.mapper.brandToBrandEntity(brand));

        log.info("Brand of car: {}", carUpdate.getBrand().getName());
        log.info("Car description: {}", carUpdate.getDescription());
        log.info("Brand id: {}", carUpdate.getBrand().getId());
        //log.info(carUpdate.toString());

        return CarEntityMapper.mapper.carEntityToCar(
                carRepository.save(CarEntityMapper.mapper.carToCarEntity(carUpdate)));
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

        Page<Car> pageCar = CarEntityMapper.mapper.carEntityPageToCarPage
                (carRepository.findAll(pageRequest));

        return pageCar;
    }
}
