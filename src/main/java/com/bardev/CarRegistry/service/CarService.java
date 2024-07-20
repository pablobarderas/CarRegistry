package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.service.model.Car;
import org.springframework.data.domain.Page;

import java.sql.Blob;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CarService {

    CompletableFuture<List<Car>> getCars();
    CompletableFuture<List<Car>> addCars(List<Car> carList);
    Car addCar(Car car);
    Car getCarById(Integer id);
    Car updateCar(Integer id, Car car);
    void deleteCar(Integer id);

    Page<Car> findAllPageable(Integer pageNumber, Integer pageSize);

    String getCarsCsv();
}
