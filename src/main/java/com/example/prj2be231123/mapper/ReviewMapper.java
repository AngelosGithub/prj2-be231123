package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Review;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewMapper {

    @Insert("""
            INSERT INTO review (title, recommend, content, writer, restaurantId)
            VALUES (#{title}, #{recommend}, #{content}, #{writer}, #{restaurantId})
            """)
    int insert(Review review);
}
