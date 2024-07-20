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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    @Override
    public List<Car> uploadCars(MultipartFile file) {
        List<CarEntity> carEntityList = new ArrayList<>();
        List<Car> carList;
        List<BrandEntity> brands = brandRepository.findAll();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))){

            // Parse csv
            CSVParser parser = new CSVParser(br, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase().withTrim());

            // Get each csv line
            Iterable<CSVRecord> records = parser.getRecords();

            // Add attributes from csv to cars
            for (CSVRecord recordLine : records) {
                CarEntity car = new CarEntity();
                String brandName = recordLine.get("brand");

                // Check brand exist and set it
                BrandEntity brand = brandRepository
                        .findByName(brandName)
                        .orElseThrow(()-> new NoSuchElementException("Brand not found"));

                car.setBrand(brand);
                car.setModel(recordLine.get("model"));
                car.setMileage(Integer.valueOf(recordLine.get("mileage")));
                car.setPrice(Double.valueOf( recordLine.get("price")));
                car.setYear(Integer.valueOf(recordLine.get("year")));
                car.setDescription(recordLine.get("description"));
                car.setColour(recordLine.get("colour"));
                car.setFuelType(recordLine.get("fuel_type"));
                car.setNumDoors(Integer.valueOf(recordLine.get("num_doors")));

                carEntityList.add(car);
            }

            // Save entities
            carList = carEntityMapper.carEntityListToCarList(carRepository.saveAll(carEntityList));
            log.info("All users saved");


        } catch (IOException e) {
            log.error("Failed to load cars");
            throw new RuntimeException("Failed to load cars: " + e.getMessage(), e);
        }catch (NoSuchElementException e) {
            log.error("Brand not found", e);
            throw new RuntimeException("Failed to load cars: " + e.getMessage(), e);
        }
        return carList;
    }
}
