package com.bardev.CarRegistry.controller;

import com.bardev.CarRegistry.CarRegistryApplication;
import com.bardev.CarRegistry.controller.dto.CarDTO;
import com.bardev.CarRegistry.controller.mapper.CarMapper;
import com.bardev.CarRegistry.repository.entity.BrandEntity;
import com.bardev.CarRegistry.repository.entity.CarEntity;
import com.bardev.CarRegistry.service.impl.CarServiceImpl;
import com.bardev.CarRegistry.service.model.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CarRegistryApplication.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = CarRegistryApplication.class)
class CarControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CarControllerTest.class);
    @Autowired
    private CarController carController;

    @Autowired
    private CarMapper carMapper;

    @MockBean
    private CarServiceImpl carService;

    @Autowired
    private MockMvc mockMvc;

    // New brand
    private static BrandEntity mercedes;

    private static Car car1;
    private static CarDTO carDTO;


    private ObjectMapper objectMapper;


    @BeforeAll
    static void setupAll(){
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

    @BeforeEach
    void setup(){
        objectMapper = new ObjectMapper();

    }

    // TODO
    @Test
    void testGetCars(){


    }

    // GET CAR BY ID: START
    @Test
    void testGetCarByIdIsOk() throws Exception {

        // Mock get car service
        Mockito.when(carService.getCarById(Mockito.anyInt()))
                .thenReturn(carMapper.carDTOToCar(carDTO));

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
    // ADD CAR: END


    // TODO
    @Test
    void testUpdateCar(){

    }


    // TODO
    @Test
    void testAddCars(){

    }

    // TODO
    @Test
    void testDeleteCar(){

    }

    // TODO
    @Test
    void testGetCarsPage(){

    }




}
