package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.RestaurantPurpose;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RestaurantPurposeMapper {

    @Select("""
                SELECT no,name
                FROM restaurantpurpose
                WHERE no = #{no}
            """)
    RestaurantPurpose getByName(Integer no);


    @Select("""
                SELECT no
                FROM restaurantpurpose
                WHERE name= #{name}
            """)
    Integer getByNo(String name);
}
