package com.bardev.CarRegistry.controller;

import com.bardev.CarRegistry.config.AsyncConfig;
import com.bardev.CarRegistry.controller.dto.CarDTO;
import com.bardev.CarRegistry.controller.dto.CarWithBrandDTO;
import com.bardev.CarRegistry.controller.mapper.CarMapper;
import com.bardev.CarRegistry.service.CarService;
import com.bardev.CarRegistry.service.model.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/cars")
    public CompletableFuture<?> getCars(){
        return CompletableFuture.supplyAsync(() -> carService.getCars())
                .thenApply(carMapper::cfCarListToCfCarWithBrandDTOList)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("There are no cars", ex);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    // GET CAR BY ID
    @GetMapping("/cars/{id}")
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

    // ADD CAR
    @PostMapping("/cars/add")
    public ResponseEntity<CarDTO> addCar(@RequestBody CarDTO carDTO){

        try {

            CarDTO carDTOGet = carMapper.
                    carToCarDTO(carService
                            .addCar(carMapper.carDTOToCar(carDTO)));

            return ResponseEntity.ok(carDTOGet);

        }catch (Exception e){
            log.error("Internal server error adding car");
            return ResponseEntity.internalServerError().build();
        }

    }

    // UPDATE CAR
    @PutMapping("/cars/update/{id}")
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
    @DeleteMapping("/cars/delete/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Integer id){

        try{
            log.info("Deleting car with id: {}", id);
            carService.deleteCar(id);
            return ResponseEntity.ok().build();
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
    public ResponseEntity<Page<CarDTO>> getCarsPage(@PathVariable Integer pageNumber, @PathVariable Integer pageSize){
        try{
            Page<CarDTO> carDTOPage = carMapper.carPageToCarDTOPage(carService.findAllPageable(pageNumber, pageSize));
            return ResponseEntity.ok().body(carDTOPage);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

}
