package com.bardev.CarRegistry.controller;

import com.bardev.CarRegistry.controller.dto.BrandDTO;
import com.bardev.CarRegistry.controller.mapper.BrandMapper;
import com.bardev.CarRegistry.repository.mapper.BrandEntityMapper;
import com.bardev.CarRegistry.service.impl.BrandServiceImpl;
import com.bardev.CarRegistry.service.model.Brand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
public class BrandController {

    @Autowired
    BrandServiceImpl brandServiceImpl;

    @Autowired
    private BrandMapper brandMapper;

    @GetMapping("/brands")
    public ResponseEntity<List<BrandDTO>> getBrands(){
        try {
            log.info("Get all brands");
            return ResponseEntity.ok(brandMapper.brandListToBrandDTOList(brandServiceImpl.getBrands()));
        }catch (NoSuchElementException e){
            log.error("There are no brands");
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.error("Internal server error getting all brands");
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/brands/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Integer id){
        try {
            log.info("Get brandEntity by id: {}",id);
            Brand brand = brandServiceImpl.getBrandById(id);
            return ResponseEntity.ok(brandMapper.brandToBrandDTO(brand));
        }catch (NoSuchElementException e){
            log.info("No such element with id: {}",id);
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.error("Internal server error getting brandEntity by id");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/brands/add")
    public ResponseEntity<BrandDTO> addBrand(@RequestBody BrandDTO brandDTO){

        try {
            BrandDTO brandDTOGet = brandMapper.brandToBrandDTO(brandServiceImpl
                    .addBrand(brandMapper.brandDTOToBrand(brandDTO)));
                    return ResponseEntity.ok(brandDTOGet);
        }catch (Exception e){
            log.error("Internal server error adding brandEntity");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/brands/update/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable Integer id, @RequestBody BrandDTO brandDTO){

        try{
            log.info("Updating brandEntity: {}", brandDTO);
            BrandDTO brandUpdated = brandMapper
                    .brandToBrandDTO(
                            brandServiceImpl.updateBrand(id, brandMapper
                            .brandDTOToBrand(brandDTO)));
            return ResponseEntity.ok(brandUpdated);
        }catch (NoSuchElementException e){
            log.error("No such element with id: {}", id);
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.error("Internal server error updating brandEntity");
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("brands/delete/{id}")
    public ResponseEntity<BrandDTO> deleteBrand(@PathVariable Integer id){

        try{
            log.info("Deleting brandEntity with id: {}", id);
            brandServiceImpl.deleteBrand(id);
            return ResponseEntity.ok().build();
        }catch (NoSuchElementException e){
            log.info("No such element with id: {}", id);
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            log.info("Internal server error trying delete brandEntity with id: {}", id);
            return ResponseEntity.internalServerError().build();
        }

    }

}
