package com.example.prj2be231123.domain;

import lombok.Data;

import java.util.List;

@Data
public class Restaurant {

    private int no;

    private String place;

    private  String info;

    private String address;

    private  String district;

    private  String x;

    private  String y;

    private  String phone;

    private  String city;

    private  int restaurantType;

    private String writer;

    private int starPoint;

    private  Integer reviewCount;

    private String typeName;

    private List<RestaurantFile> files;

    private List<RestaurantPurpose> purpose;


}
