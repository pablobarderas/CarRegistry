package com.bardev.CarRegistry.controller;

import com.bardev.CarRegistry.CarRegistryApplication;
import com.bardev.CarRegistry.controller.dto.CarDTO;
import com.bardev.CarRegistry.controller.mapper.CarMapper;
import com.bardev.CarRegistry.repository.entity.BrandEntity;
import com.bardev.CarRegistry.repository.entity.CarEntity;
import com.bardev.CarRegistry.service.impl.CarServiceImpl;
import com.bardev.CarRegistry.service.model.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CarRegistryApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CarControllerTest.class);

    @Autowired
    private CarMapper carMapper;

    @MockBean
    private CarServiceImpl carService;

    @Autowired
    private MockMvc mockMvc;

    // New brand
    private static BrandEntity mercedes;

    private static Car car1;
    private static Car car2;
    private static CarDTO carDTO;


    private ObjectMapper objectMapper;


    @BeforeEach
    void setup(){
        objectMapper = new ObjectMapper();

        // MERCEDES
        mercedes = BrandEntity.builder()
                .id(1)
                .name("mercedes")
                .carEntityList(List.of(new CarEntity()))
                .country("Spain")
                .warranty(20)
                .build();

        // CAR1
        car1 = Car.builder()
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
        car2 = Car.builder()
                .id(2)
                .model("leon")
                .price(20_000.0)
                .colour("white")
                .brand(mercedes)
                .description("good car")
                .fuelType("diesel")
                .mileage(200_000)
                .year(2000)
                .numDoors(5)
                .build();

        // CARDTO
        carDTO = CarDTO.builder()
                .id(1)
                .model("altea")
                .price(20_000.0)
                .colour("red")
                .brandName("mercedes")
                .description("good car")
                .fuelType("diesel")
                .mileage(200_000)
                .year(2000)
                .numDoors(5)
                .build();

    }

    // GET CARS: START

    @Test
    void testGetCarsUnauthorized() throws Exception {
        CompletableFuture<List<Car>> carList = CompletableFuture.completedFuture(List.of(car1, car2));

        // Mock get car
        Mockito.when(carService.getCars())
                .thenReturn(carList);

        this.mockMvc
                .perform(get("/cars"))
                .andExpect(status().isForbidden());
    }
/*
    @Test
    void testGetCarsIsOk() throws Exception {
        CompletableFuture<List<Car>> carList = CompletableFuture.completedFuture(List.of(car1, car2));
        List<CarWithBrandDTO> carWithBrandDTOList = List.of(
                carMapper.carToCarWithBrandDTO(car1),
                carMapper.carToCarWithBrandDTO(car2)
        );

        String responseJson = objectMapper.writeValueAsString(carWithBrandDTOList);

        // Mock get cars service
        Mockito.when(carService.getCars())
                .thenReturn(carList);

        // Expected car attributes
        this.mockMvc
                .perform(get("/cars")
                        .with(user("client").roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseJson))
                .andDo(result -> log.info("Response JSON: " + result.getResponse().getContentAsString()));
    }

    @Test
    void testGetCarsNoSuchElementException() throws Exception {

        // Mock no such element exception
        Mockito.when(carService.getCars())
                .thenThrow(NoSuchElementException.class);

        this.mockMvc
                .perform(get("/cars")
                        .with(user("client").roles("CLIENT")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCarsThrowsException() throws Exception {

        // Mock get car
        Mockito.when(carService.getCars())
                .thenThrow(RuntimeException.class);

        this.mockMvc
                .perform(get("/cars")
                        .with(user("client").roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    */
    // GET CARS: END


    // GET CAR BY ID: START
    @Test
    void testGetCarByIdIsOk() throws Exception {

        // Mock get car service
        Mockito.when(carService.getCarById(Mockito.anyInt()))
                .thenReturn(carMapper.carDTOToCar(carDTO));// Mock get car service

        // Expected car attributes
        this.mockMvc
                .perform(get("/car/{1}", 1)
                        .with(user("client").roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.model").value("altea"));
    }

    @Test
    void testGetCarByIdNoSuchElementException() throws Exception {

        // Mock no such element exception
        Mockito.when(carService.getCarById(Mockito.anyInt()))
                .thenThrow(NoSuchElementException.class);

        this.mockMvc
                .perform(get("/car/{1}", 1)
                        .with(user("client").roles("CLIENT")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCarByIdUnauthorized() throws Exception {

        // Mock get car
        Mockito.when(carService.getCarById(Mockito.anyInt()))
                .thenReturn(carMapper.carDTOToCar(carDTO));

        this.mockMvc
                .perform(get("/car/{1}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetCarByIdThrowsException() throws Exception {

        // Mock get car
        Mockito.when(carService.getCarById(Mockito.anyInt()))
                .thenThrow(RuntimeException.class);

        this.mockMvc
                .perform(get("/car/{1}", 1)
                        .with(user("client").roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
    // GET CAR BY ID: END


    // ADD CAR: START
    @Test
    void testAddCarIsOk() throws Exception{

        // Parse CarDTO to JSON
        String carDTOJson = objectMapper.writeValueAsString(carDTO);
        log.info("JSON carDTO -> {}", carDTOJson);

        // Check car service
        Mockito.when(carService
                        .addCar(Mockito.any(Car.class)))
                .thenReturn(car1);

        // Test post method with vendor role and expect isOk status and car response
        this.mockMvc
                .perform(post("/car/add")
                        .with(user("vendor").roles("VENDOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carDTOJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(carDTOJson));

    }

    @Test
    void testAddCarThrowsNoSuchElementException() throws Exception {
        CarDTO carDTOWithoutBrand = new CarDTO();
        carDTO.setBrandName("Unknown");

        String carJson = objectMapper.writeValueAsString(carDTOWithoutBrand);

        Mockito.when(carService.addCar(Mockito.any(Car.class)))
                .thenThrow(NoSuchElementException.class);

        this.mockMvc
                .perform(post("/car/add")
                        .with(user("vendor").roles("VENDOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddCarUnauthorized() throws Exception {

        String carJson = objectMapper.writeValueAsString(carDTO);

        // Test rest without role
        this.mockMvc
                .perform(post("/car/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAddCarThrowsException() throws Exception {

        String carJson = objectMapper.writeValueAsString(carDTO);

        // Mock RuntimeException
        Mockito.when(carService.addCar(Mockito.any(Car.class)))
                .thenThrow(RuntimeException.class);

        this.mockMvc
                .perform(post("/car/add")
                        .with(user("vendor").roles("VENDOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isInternalServerError());
    }

    // Test invalid request
    /*
    @Test
    void testAddCarWithInvalidInput() throws Exception {
        CarDTO carDTOEmpty = new CarDTO();
        carDTOEmpty.setBrandName("");

        String carJson = objectMapper.writeValueAsString(carDTOEmpty);

        this.mockMvc
                .perform(post("/car/add")
                        .with(user("vendor").roles("VENDOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isBadRequest());
    }
*/

    // ADD CAR: END


    // UPDATE CAR: START
    @Test
    void testUpdateCarIsOk() throws Exception{

        // Parse CarDTO to JSON
        String carDTOJson = objectMapper.writeValueAsString(carDTO);
        log.info("JSON carDTO update -> {}", carDTOJson);

        // Check car service
        Mockito.when(carService
                        .updateCar(Mockito.anyInt(), Mockito.any(Car.class)))
                .thenReturn(car1);

        // Test post method with vendor role and expect isOk status and car response
        this.mockMvc
                .perform(put("/car/update/{1}", 1)
                        .with(user("vendor").roles("VENDOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carDTOJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(carDTOJson));

    }

    @Test
    void testUpdateCarThrowsNoSuchElementException() throws Exception {
        CarDTO carDTOWithoutBrand = new CarDTO();
        carDTO.setBrandName("Unknown");

        String carJson = objectMapper.writeValueAsString(carDTOWithoutBrand);

        Mockito.when(carService.updateCar(Mockito.anyInt(), Mockito.any(Car.class)))
                .thenThrow(NoSuchElementException.class);

        this.mockMvc
                .perform(put("/car/update/{1}", 1)
                        .with(user("vendor").roles("VENDOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCarUnauthorized() throws Exception {

        String carJson = objectMapper.writeValueAsString(carDTO);

        // Test rest without role
        this.mockMvc
                .perform(put("/car/update/{1}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateCarThrowsException() throws Exception {

        String carJson = objectMapper.writeValueAsString(carDTO);

        // Mock RuntimeException
        Mockito.when(carService.updateCar(Mockito.anyInt(), Mockito.any(Car.class)))
                .thenThrow(RuntimeException.class);

        this.mockMvc
                .perform(put("/car/update/{1}", 1)
                        .with(user("vendor").roles("VENDOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isInternalServerError());
    }
    // UPDATE CAR: END



    // TODO
   /* @Test
    void testAddCars(){

    }*/

    // DELETE CAR: START
    @Test
    void testDeleteCarSuccess() throws Exception {
        // Mock the delete service call
        Mockito.doNothing().when(carService).deleteCar(1);

        this.mockMvc
                .perform(delete("/car/delete/{id}", 1)
                        .with(user("vendor").roles("VENDOR")))
                .andExpect(status().isNoContent());

    }

    @Test
    void testDeleteCarThrowsNoSuchElementException() throws Exception {
        Mockito.doThrow(NoSuchElementException.class).when(carService).deleteCar(1);

        this.mockMvc
                .perform(delete("/car/delete/{1}", 1)
                        .with(user("vendor").roles("VENDOR")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCarUnauthorized() throws Exception {

        // Test rest without role
        this.mockMvc
                .perform(delete("/car/delete/{1}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteCarThrowsException() throws Exception {

        // Mock RuntimeException
        Mockito.doThrow(RuntimeException.class)
                .when(carService).deleteCar(1);

        this.mockMvc
                .perform(delete("/car/delete/{1}", 1)
                        .with(user("vendor").roles("VENDOR")))
                .andExpect(status().isInternalServerError());
    }

    // DELETE CAR: END




}
