package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.service.model.Car;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CarService {

    CompletableFuture<List<Car>> getCars();
    Car getCarById(Integer id);
    CompletableFuture<List<Car>> addCars(List<Car> carList);
    Car updateCar(Integer id, Car car);
    void deleteCar(Integer id);

    Page<Car> findAllPageable(Integer pageNumber, Integer pageSize);
}
