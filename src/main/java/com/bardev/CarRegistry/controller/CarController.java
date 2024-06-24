package com.bardev.CarRegistry.controller;

import com.bardev.CarRegistry.controller.dto.CarDTO;
import com.bardev.CarRegistry.controller.dto.CarWithBrandDTO;
import com.bardev.CarRegistry.controller.mapper.CarMapper;
import com.bardev.CarRegistry.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/cars")
    public ResponseEntity<List<CarWithBrandDTO>> getCars(){
        try {
            log.info("Get all cars");
            return ResponseEntity.ok(CarMapper.mapper.carListToCarWithBrandDTOList(carService.getCars()));
        }catch (NoSuchElementException e){
            log.error("There are no cars");
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.error("Internal server error getting all cars");
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET CAR BY ID
    @GetMapping("/cars/{id}")
    public ResponseEntity<CarWithBrandDTO> getCarById(@PathVariable Integer id){

        try {
            log.info("Get car by id: {}",id);
            return ResponseEntity.ok(CarMapper.mapper.carToCarWithBrandDTO(carService.getCarById(id)));
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

            CarDTO carDTOGet = CarMapper.mapper.
                    carToCarDTO(carService
                            .addCar(CarMapper.mapper.carDTOToCar(carDTO)));

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
            CarDTO carUpdated = CarMapper.mapper.carToCarDTO(
                    carService.updateCar(id, CarMapper.mapper.carDTOToCar(carDTO)));
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
            Page<CarDTO> carDTOPage = CarMapper.mapper.carPageToCarDTOPage(carService.findAllPageable(pageNumber, pageSize));
            return ResponseEntity.ok().body(carDTOPage);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

}
