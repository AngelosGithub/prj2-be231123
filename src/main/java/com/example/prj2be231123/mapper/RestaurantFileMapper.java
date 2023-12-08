package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.RestaurantFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface RestaurantFileMapper {

    @Insert("""
            INSERT INTO restaurantfile(restaurantNo, fileName)
            VALUES (#{restaurantNo},#{filename}) 
            """)
    int insert(int restaurantNo, String filename);


    @Select("""
                    SELECT no,restaurantNo, fileName
                    FROM restaurantfile
                    WHERE restaurantNo= #{restaurantNo}
            """)
    List<RestaurantFile> selectNamesById(int restaurantNo);





    @Delete("""
                DELETE FROM restaurantfile
                WHERE restaurantNo = #{restaurantNo}
            """)
    int deleteByFileRestaurantdNo(Integer restaurantNo);

    @Select("""
                SELECT *
                FROM restaurantfile
                WHERE restaurantNo = #{restaurantNo}
                LIMIT 1
            """)
    List<RestaurantFile> selectAllNamesById(int restaurantNo);


    @Select("""
                SELECT *FROM restaurantfile
                WHERE no=#{no}
            """)
    RestaurantFile selectByNo(Integer no);

    @Delete("""
                DELETE FROM restaurantfile
                WHERE no = #{no}
            """)
    int deleteById(Integer no);



    @Select("""
               SELECT *
                FROM restaurantfile
                WHERE restaurantNo = #{restaurantNo}
                LIMIT 1
            """)
    List<RestaurantFile> selectNameById(int restaurantNo);
}