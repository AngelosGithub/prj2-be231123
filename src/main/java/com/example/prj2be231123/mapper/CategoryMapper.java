package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.RestaurantPurpose;
import com.example.prj2be231123.domain.RestaurantType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {



    @Select("""
                SELECT  *
                FROM restauranttypes
            """)
    List<RestaurantType> getTypes();


    @Select("""
                SELECT *
                FROM restaurantpurpose
            """)
    List<RestaurantPurpose> getPurpose();

    @Select("""
                SELECT no
                FROM restauranttypes
                WHERE name = #{restaurantTypeName}
            """)
    int getTypeNo(String restaurantTypeName);


    @Select("""
                SELECT no
                FROM restaurantpurpose
                WHERE name = #{name}
            """)
    int getPurposeNo(String name);






    @Insert("""
                INSERT INTO  restauranttypes(name) 
                VALUES ( #{name})
            """)
    int typesAdd(RestaurantType name);

    @Insert("""
                INSERT INTO  restaurantpurpose(name)
                VALUES ( #{name})
         """)
    int purposeAdd(RestaurantPurpose name);


    @Select("""
                SELECT name
                FROM restauranttypes
                WHERE name = #{name}
        """)
    String selectTypeName(String name);

    @Select("""
            SELECT name
            FROM  restaurantpurpose
            WHERE name = #{name}
        """)
    String selectPurposeName(String name);


    @Select("""
                SELECT *
                FROM restauranttypes
                WHERE no = #{no}
            """)
    RestaurantType getByNoType(Integer no);

    @Update("""
                UPDATE restauranttypes
                SET name = #{name}
                WHERE no = #{no}
            """)
    int typeUpdate(RestaurantType restaurantType);


    @Select("""
                SELECT * 
                FROM restaurantpurpose
                WHERE no = #{no}
            """)
    RestaurantPurpose getByNoPurpose(Integer no);


    @Update("""
                UPDATE restaurantpurpose
                SET name =#{name}
                WHERE no = #{no}
            """)
    int purposeUpdate(RestaurantPurpose restaurantPurpose);


    @Delete("""
                DELETE FROM restauranttypes
                WHERE no = #{no}
            """)
    int typeRemove(Integer no);

    @Delete("""
                DELETE FROM restaurantpurpose
                WHERE no= #{no}
            """)
    int purposeRemove(Integer no);

    @Select("""
                SELECT name
                FROM restauranttypes           
            """)
    List<String> getTypesName();





    @Select("""
    SELECT rs.no,rs.name,count(rs.name) AS count
    FROM restaurant rt LEFT JOIN restauranttypes rs
        ON  rt.restaurantType = rs.no
    GROUP BY rs.name
    ORDER BY rs.name DESC;
    """)
    List<RestaurantType> getCount();
}