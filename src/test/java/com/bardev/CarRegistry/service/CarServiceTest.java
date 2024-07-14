package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.CarRegistryApplication;
import com.bardev.CarRegistry.repository.BrandRepository;
import com.bardev.CarRegistry.repository.CarRepository;
import com.bardev.CarRegistry.repository.entity.BrandEntity;
import com.bardev.CarRegistry.repository.entity.CarEntity;
import com.bardev.CarRegistry.repository.mapper.CarEntityMapper;
import com.bardev.CarRegistry.service.impl.CarServiceImpl;
import com.bardev.CarRegistry.service.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CarRegistryApplication.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = CarRegistryApplication.class)
public class CarServiceTest {

    @InjectMocks
    private CarServiceImpl carService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarEntityMapper carEntityMapper;

    private CarEntity carEntity;
    private Car car;
    private BrandEntity mercedes;


    @BeforeEach
    void setup(){
        // MERCEDES
        mercedes = BrandEntity.builder()
                .id(1)
                .name("mercedes")
                .carEntityList(List.of(new CarEntity()))
                .country("Spain")
                .warranty(20)
                .build();

        // CAR1
        carEntity = CarEntity.builder()
                .id(1)
                .model("altea")
                .price(20_000.0)
                .colour("red")
                .brand(mercedes)
                .description("good car")
                .fuelType("diesel")
                .mileage(200_000)
                .year(2000)
                .numDoors(5)
                .build();

        // CAR2
        car = Car.builder()
                .id(1)
                .model("altea")
                .price(20_000.0)
                .colour("red")
                .brand(mercedes)
                .description("good car")
                .fuelType("diesel")
                .mileage(200_000)
                .year(2000)
                .numDoors(5)
                .build();

    }

    @Test
    void testAddCar() {

        // Mock find brand
        Mockito.when(brandRepository.findByName(carEntity.getBrand().getName())).thenReturn(Optional.of(mercedes));

        // Mock repository save
        Mockito.when(carRepository.save(Mockito.any(CarEntity.class))).thenReturn(carEntity);

        // Mock mapper car to car entity and reverse
        Mockito.when(carEntityMapper.carToCarEntity(car)).thenReturn(carEntity);
        Mockito.when(carEntityMapper.carEntityToCar(carEntity)).thenReturn(car);

        Car result = carService.addCar(carEntityMapper.carEntityToCar(carEntity));

        // Test attributes
        assertNotNull(result);
        assertEquals(carEntity.getId(), result.getId());
        assertEquals(carEntity.getBrand(), result.getBrand());
        assertEquals(carEntity.getModel(), result.getModel());
        assertEquals(carEntity.getColour(), result.getColour());
        assertEquals(carEntity.getYear(), result.getYear());
        assertEquals(carEntity.getPrice(), result.getPrice());
        assertEquals(carEntity.getMileage(), result.getMileage());
        assertEquals(carEntity.getNumDoors(), result.getNumDoors());
        assertEquals(carEntity.getDescription(), result.getDescription());
        assertEquals(carEntity.getFuelType(), result.getFuelType());

    }

    @Test
    void testGetCarById() {

        // Mock find brand
        Mockito.when(brandRepository.findByName(carEntity.getBrand().getName())).thenReturn(Optional.of(mercedes));

        // Mock repository get by id
        Mockito.when(carRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(carEntity));

        // Mock mapper car to car entity and reverse
        Mockito.when(carEntityMapper.carToCarEntity(car)).thenReturn(carEntity);
        Mockito.when(carEntityMapper.carEntityToCar(carEntity)).thenReturn(car);

        // Get car
        Car result = carService.getCarById(1);

        // Test attributes
        assertNotNull(result);
        assertEquals(carEntity.getId(), result.getId());
        assertEquals(carEntity.getBrand(), result.getBrand());
        assertEquals(carEntity.getModel(), result.getModel());
        assertEquals(carEntity.getColour(), result.getColour());
        assertEquals(carEntity.getYear(), result.getYear());
        assertEquals(carEntity.getPrice(), result.getPrice());
        assertEquals(carEntity.getMileage(), result.getMileage());
        assertEquals(carEntity.getNumDoors(), result.getNumDoors());
        assertEquals(carEntity.getDescription(), result.getDescription());
        assertEquals(carEntity.getFuelType(), result.getFuelType());

    }


    @Test
    void testGetAllCars() throws ExecutionException, InterruptedException {

        List<Car> carList = List.of(car, car);
        List<CarEntity> carEntityList = List.of(carEntity, carEntity);

        // CompletableFuture mock that service return
        //CompletableFuture<List<Car>> futureCarList = CompletableFuture.completedFuture(carList);

        // Mock future list
        Mockito.when(carService.getCars()).thenReturn(CompletableFuture.completedFuture(carList));

        // Mock lists mappers
        Mockito.when(carEntityMapper.carListToCarEntityList(carList)).thenReturn(carEntityList);
        Mockito.when(carEntityMapper.carEntityListToCarList(carEntityList)).thenReturn(carList);



        // Get all cars
        CompletableFuture<List<Car>> resultFuture = carService.getCars();

        // Get results
        List<Car> result = resultFuture.get();

        // Check results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(car.getId(), result.get(0).getId());
    }





}
