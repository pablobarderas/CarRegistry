package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.service.model.Car;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarService {

    List<Car> getCars();
    Car getCarById(Integer id);
    Car addCar(Car car);
    Car updateCar(Integer id, Car car);
    void deleteCar(Integer id);

    Page<Car> findAllPageable(Integer pageNumber, Integer pageSize);
}
