package com.example.prj2be231123.controller;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.Restaurant;
import com.example.prj2be231123.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;
    @Value("${kakao.map.api.key}")
    private String mapKey;


    @GetMapping("map/key")
    public String key() {
        return mapKey;
    }


    @PostMapping("add")
    public ResponseEntity add(
            Restaurant restaurant,
            @RequestParam(value = "restaurantTypeName" , required = false) String restaurantTypeName,
            @RequestParam(value = "checkBoxIds[]" , required = false)List<String> restaurantPurpose,
            @RequestParam(value = "uploadFiles[]" ,required = false)MultipartFile[] files,
            @SessionAttribute(value = "login",required = false)Member login
            )throws IOException  {

        if(login==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
        }


        if(!service.hasAccess(login)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(!service.restaurantValidate(restaurant,restaurantTypeName,restaurantPurpose,files)){
            return ResponseEntity.badRequest().build();
        }


        if(service.save(restaurant,restaurantTypeName,restaurantPurpose,files,login)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("list")
    public  HashMap<String,Object> list(
            @RequestParam(value = "p",defaultValue = "1") Integer page
            ,@RequestParam(value= "k",defaultValue = "") String keyword,
            @RequestParam(value = "c",defaultValue = "all") String category,
            @RequestParam(value = "purpose",defaultValue = "")List<String>  checkBoxIds
            ,@RequestParam(value ="typeno",defaultValue = "0")Integer typeno
    ){



        return service.selectAll(page,keyword,category,checkBoxIds,typeno);
    }


    @GetMapping("no/{no}")
    public HashMap<String, Object> get(
            @PathVariable Integer no
    ){

        return service.get(no);
    }


    @DeleteMapping("remove/{no}")
    public ResponseEntity remove(
            @PathVariable Integer no,
            @SessionAttribute(value = "login",required = false) Member login
    ){
        if(login==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
        }


        if(!service.hasAccess(login)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }



        if (service.remove(no)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("update")
    public ResponseEntity update(
            Restaurant restaurant,
            @RequestParam(value = "restaurantTypeName" , required = false) String restaurantTypeName,
            @RequestParam(value = "checkBoxIds[]" , required = false)List<String> restaurantPurpose,
            @RequestParam(value = "removeFileIds[]" , required = false)List<Integer> removeFileIds,
            @RequestParam(value = "uploadFiles[]" ,required = false)MultipartFile[] uploadFiles,
            @SessionAttribute(value = "login",required = false) Member login
    )throws IOException{

        if(login==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
        }


        if(!service.hasAccess(login)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(!service.restaurantValidate(restaurant,restaurantTypeName,restaurantPurpose,uploadFiles)){
            return ResponseEntity.badRequest().build();
        }

        if(service.update(restaurant,restaurantTypeName,restaurantPurpose,removeFileIds,uploadFiles)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.internalServerError().build();
        }

    }


}
