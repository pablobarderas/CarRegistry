package com.bardev.CarRegistry.controller;

import com.bardev.CarRegistry.service.impl.CarServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class CarControllerTest {

    @Autowired
    private CarController carController;

    @MockBean
    private CarServiceImpl carService;

    @Autowired
    private MockMvc mockMvc;

    // TODO
    @Test
    void testGetCars(){

    }

    // TODO
    @Test
    void testGetCarById(){

    }

    // TODO
    @Test
    void testAddCar(){

    }

    // TODO
    @Test
    void testAddCars(){

    }

    // TODO
    @Test
    void testUpdateCar(){

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
