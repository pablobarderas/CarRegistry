package com.bardev.CarRegistry.repository.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "car")
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    private String model;
    private Integer mileage;
    private Double price;
    private Integer year;
    private String description;
    private String colour;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "num_doors")
    private Integer numDoors;

}
