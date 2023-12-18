package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.RestaurantPurpose;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RestaurantPurposeJoinMapper {

    @Insert("""
                INSERT INTO restaurantjoin(purposeNo, restaurantNo) 
                VALUES (#{purposeNo}, #{restaurantNo})
            
            """)
    int insert(int purposeNo, int restaurantNo );


    @Select("""
            SELECT *
            FROM restaurantjoin
            WHERE restaurantNo
            """)
    List<RestaurantPurpose> selectById(Integer no);

    @Select("""
                SELECT purposeNo
                FROM restaurantjoin
                WHERE restaurantNo = #{restaurantNo}
            """)
    List<Integer> getPurposeById(Integer restaurantNo);

    @Delete("""
                DELETE FROM restaurantjoin
                WHERE restaurantNo = #{restaurantNo}
            
            """)
    int deleteByRestaurantNo(Integer restaurantNo);

   

    @Select("""
                SELECT restaurantNo
                FROM restaurantjoin
                WHERE purposeNo = #{purposeNo}
            """)
    List<Integer> selectByRestaurantNo(Integer purposeNo);


    @Select("""
                SELECT DISTINCT (restaurantNo)
                FROM restaurantjoin
                WHERE purposeNo = #{purposeNo}
                ORDER BY  no DESC 
                
            """)
    List<Integer> selectByRestaurantsNo(Integer purposeNo);
}
