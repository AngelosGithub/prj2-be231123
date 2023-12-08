package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Restaurant;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RestaurantMapper {


    @Insert("""
                INSERT INTO  restaurant(
                 writer,place, info, address, district, y, x, phone, city,restaurantType)
                VALUES (
                 #{writer},#{place},#{info},#{address},#{district},#{y},
                #{x},#{phone},#{city},#{restaurantType}
                )
            """)
    @Options(useGeneratedKeys = true,keyProperty = "no")
    int save(Restaurant restaurant);

    @Select("""
             SELECT 
             r.no, 
             r.place, 
             r.info, 
             r.address,
              r.district, 
              r.y, r.x, 
              r.phone, 
              r.restaurantType,
              r.city,
             avg(st.point) starPoint
             FROM restaurant r  join review rv
                 on r.no = rv.restaurantId
               join starpoint st
                  on st.reviewId = rv.no
             WHERE r.no= #{no};
            """)
    Restaurant getId(Integer no);

    @Select("""
           <script>
                SELECT
                rt.no,
                rt.place,
                rt.info,
                rt.address,
                rt.district,
                rt.y, rt.x,
                rt.phone,
                rt.restaurantType,
                rt.city,
                AVG(st.point) starPoint
                FROM restaurant rt left JOIN review rv
                on rt.no = rv.restaurantId
                left join starpoint st
                on st.reviewId = rv.no
           <where>
           <trim prefixOverrides="OR">
              <choose>
               <when test="restaurantType != 0">
                     restaurantType= #{restaurantType}
               </when>
             
                <when test="category == 'all' or category == 'place' ">
                            OR place LIKE #{keyword}
                </when>
                             
                    <when test="category == 'all' or category == 'district'">
                            OR district LIKE #{keyword}
                    </when>
               
                 </choose>
           </trim>
          </where>
          GROUP BY rt.no
          ORDER BY  rt.no DESC
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
                 <choose>
                 <when test="restaurantType != 0">
                     restaurantType= #{restaurantType}
               </when>
             
                   <when test="category == 'all' or category == 'place' ">
                            OR place LIKE #{keyword}
                    </when>
                             
                    <when test="category == 'all' or category == 'district'">
                            OR district LIKE #{keyword}
                    </when>
               
                 </choose>
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


    @Select("""
                SELECT rt.no,rt.place,rs.name AS typeName
                FROM restaurant rt LEFT JOIN restauranttypes rs
                ON  rt.restaurantType = rs.no
                WHERE name=  #{name}
                ORDER BY rs.name DESC
                LIMIT 3;
            """)
    List<Restaurant> getTypeName(String name);


    @Select("""
           
            SELECT *
            FROM restaurant
            WHERE restaurantType =#{restaurantType}
            """)
    List<Restaurant> getDetail(Integer restaurantType);
}