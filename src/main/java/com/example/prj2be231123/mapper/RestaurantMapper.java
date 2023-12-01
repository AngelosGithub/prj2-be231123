package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Restaurant;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RestaurantMapper {


    @Insert("""
                INSERT INTO  restaurant(
                place, info, address, district, y, x, phone, city,restaurantType)
                VALUES (
                #{place},#{info},#{address},#{district},#{y},
                #{x},#{phone},#{city},#{restaurantType}
                )
            """)
    @Options(useGeneratedKeys = true,keyProperty = "no")
    int save(Restaurant restaurant);

    @Select("""
                SELECT *
                FROM restaurant
                WHERE no=#{no}
            """)
    Restaurant getId(Integer no);

    @Select("""
           <script>
                SELECT *
                FROM restaurant
           <where>  
           <trim prefixOverrides="OR">
               <if test="restaurantType != 0">
                       restaurantType= #{restaurantType}
               </if>
           </trim>
          </where>
                ORDER BY  no DESC 
                    LIMIT #{from}, 6;
           </script>
            """)
    List<Restaurant>selectAll(Integer from,Integer restaurantType,String keyword, String category);

    @Delete("""
                DELETE FROM restaurant
                WHERE no = #{no}
            """)
    int deleteByNo(Integer no);

    @Select("""
            <script>
                SELECT COUNT(*)
                FROM restaurant
              <where>  
                <trim prefixOverrides="OR">
                   <if  test="restaurantType != 0">
                     restaurantType= #{restaurantType}
                   </if>
          
          
                </trim>
             </where>  
            </script>
            """)
    int countAll(Integer restaurantType,String keyword, String category);

    @Update("""
                UPDATE restaurant
                SET 
                place =  #{place},
                info = #{info},
                address = #{address},
                district = #{district},
                y= #{y},
                x=#{x},
                phone =#{phone},
                city = #{city},
                restaurantType = #{restaurantType} 
                 WHERE no = #{no}      
            """)
    int update(Restaurant restaurant);

    @Select("""
                SELECT no
                FROM  restaurant
                WHERE restaurantType = #{no}
            """)
    List<Integer> selectListByCategoryNo(Integer no);


    @Select("""
             
                SELECT *
                FROM restaurant
                WHERE no = #{no}
                ORDER BY  no DESC 
            

            """)
    List<Restaurant> getIdSelectAll(Integer no);
}
