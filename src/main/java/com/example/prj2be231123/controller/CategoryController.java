package com.example.prj2be231123.controller;


import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.RestaurantPurpose;
import com.example.prj2be231123.domain.RestaurantType;
import com.example.prj2be231123.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;


    @GetMapping("list")
    public Map<String,Object> categoryList(){

        return service.getCategoryList();
    }

    @PostMapping("add/restaurantTypes")
    public ResponseEntity addTypes(
            @RequestBody RestaurantType restaurantType,
            @SessionAttribute(value = "login",required = false)Member login
            ){

        if(login==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
        }


        if(!service.hasAccess(login)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(!service.restaurantTypeValidate(restaurantType)){
            return ResponseEntity.badRequest().build();
        }

        if(service.addTypes(restaurantType)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("add/restaurantPurpose")
    public ResponseEntity addPurpose(
            @RequestBody RestaurantPurpose restaurantPurpose
    ){

        if(!service.restaurantPurposeValidate(restaurantPurpose)){
            return ResponseEntity.badRequest().build();
        }

        if(service.addPurpose(restaurantPurpose)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("gettype/{no}")
    public RestaurantType getTypes(
            @PathVariable Integer no
    ){
        return service.getTypes(no);
    }

    @PutMapping("update/restaurantTypes")
    public ResponseEntity typesUpdate(
            @RequestBody RestaurantType restaurantType
    ){
        if (!service.restaurantTypeValidate(restaurantType)){
          return   ResponseEntity.badRequest().build();
        }

        if (service.typesUpdate(restaurantType)){
          return   ResponseEntity.ok().build();
        }else{
          return   ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("getpurpose/{no}")
    public RestaurantPurpose getPurpose(
            @PathVariable Integer no
    ){
        System.out.println("no = " + no);
        return service.getPurpose(no);
    }

    @PutMapping("update/restaurantpurpose")
    public ResponseEntity purposeUpdate(
            @RequestBody RestaurantPurpose restaurantPurpose
    ){
        if(!service.restaurantPurposeValidate(restaurantPurpose)){
            return ResponseEntity.badRequest().build();
        }

        if (service.purposeUpdate(restaurantPurpose)){
            return ResponseEntity.ok().build();
        }else {
            return  ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("remove/restaurantTypes/{no}")
    public ResponseEntity typesRemove(
            @PathVariable Integer no
    ){
        if(service.typeRemove(no)){
            return  ResponseEntity.ok().build();
        }else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("remove/restaurantpurpose/{no}")
    public ResponseEntity purposeRemove(
            @PathVariable Integer no
    ){
        System.out.println("no = " + no);
        if(service.purposeRemove(no)){
            return  ResponseEntity.ok().build();
        }else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
