package com.bardev.CarRegistry.controller;

import com.bardev.CarRegistry.config.AsyncConfig;
import com.bardev.CarRegistry.controller.dto.CarDTO;
import com.bardev.CarRegistry.controller.dto.CarWithBrandDTO;
import com.bardev.CarRegistry.controller.mapper.CarMapper;
import com.bardev.CarRegistry.service.CarService;
import com.bardev.CarRegistry.service.model.Car;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private CarMapper carMapper;

    // GET CARS (ASYNC)
    @GetMapping("/cars")
    @PreAuthorize("hasRole('CLIENT')")
    public CompletableFuture<?> getCars(){

            log.info("Get all cars");

            // Get cars
             return carService.getCars()
                    .thenApply(carList -> {
                        List<CarWithBrandDTO> carDTOList = carMapper.carListToCarWithBrandDTOList(carList);
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(carDTOList);
                    }).exceptionally(ex -> {
                         if (ex.getCause() instanceof NoSuchElementException) {
                             return ResponseEntity.notFound().build();
                         }
                         return ResponseEntity.internalServerError().build();
                     });


    }

    // GET CAR BY ID
    @GetMapping("/car/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CarWithBrandDTO> getCarById(@PathVariable Integer id){

        try {
            log.info("Get car by id: {}",id);
            return ResponseEntity.ok(carMapper.carToCarWithBrandDTO(carService.getCarById(id)));
        }catch (NoSuchElementException e){
            log.info("No such element with id: {}",id);
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.error("Internal server error getting car by id");
            return ResponseEntity.internalServerError().build();
        }
    }

    // ADD CARS (ASYNC)
    @PostMapping("/cars/add")
    @PreAuthorize("hasRole('VENDOR')")
    public CompletableFuture<?> addCar(@RequestBody List<CarDTO> carListDTO){

        try {
            // Add and get all cars
            CompletableFuture<List<Car>> carList =
                    carService.addCars(carMapper.carDTOListToCarList(carListDTO));

            // Parse carList to response list
            List<CarDTO> response = carList.get()
                    .stream()
                    .map(carMapper::carToCarDTO)
                    .toList();

            return CompletableFuture.completedFuture(response)
                    .thenApply(ResponseEntity::ok);


        }catch (Exception e){
            log.error("Internal server error adding car");
            return CompletableFuture.failedFuture(e);
        }

    }

    // ADD CAR
    @PostMapping("/car/add")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<CarDTO> addCar(@RequestBody CarDTO carDTO){

        try{
            log.info("Adding car: {}", carDTO);
            CarDTO carAdded = carMapper.carToCarDTO(
                    carService.addCar(carMapper.carDTOToCar(carDTO)));
            return ResponseEntity.ok(carAdded);
        }catch (NoSuchElementException e){
            log.error("No such element on database");
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.error("Internal server error adding car");
            return ResponseEntity.internalServerError().build();
        }
    }

    // UPDATE CAR
    @PutMapping("/car/update/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Integer id, @RequestBody CarDTO carDTO){

        try{
            log.info("Updating car: {}", carDTO);
            CarDTO carUpdated = carMapper.carToCarDTO(
                    carService.updateCar(id, carMapper.carDTOToCar(carDTO)));
            return ResponseEntity.ok(carUpdated);
        }catch (NoSuchElementException e){
            log.error("No such element with id: {}", id);
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.error("Internal server error updating car");
            return ResponseEntity.internalServerError().build();
        }
    }

    // DELETE METHOD
    @DeleteMapping("/car/delete/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> deleteCar(@PathVariable Integer id){

        try{
            log.info("Deleting car with id: {}", id);
            carService.deleteCar(id);
            return ResponseEntity.noContent().build();
        }catch (NoSuchElementException e){
            log.info("No such element with id: {}", id);
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.info("Internal server error trying delete car with id: {}", id);
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET PAGE
    @GetMapping("/cars/page/{pageNumber}/size/{pageSize}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Page<CarDTO>> getCarsPage(@PathVariable Integer pageNumber, @PathVariable Integer pageSize){
        try{
            Page<CarDTO> carDTOPage = carMapper.carPageToCarDTOPage(carService.findAllPageable(pageNumber, pageSize));
            return ResponseEntity.ok().body(carDTOPage);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

}
