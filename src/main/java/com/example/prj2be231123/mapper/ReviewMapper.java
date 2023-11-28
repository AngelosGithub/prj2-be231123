package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Insert("""
            INSERT INTO review (title, recommend, content, writer, restaurantId)
            VALUES (#{title}, #{recommend}, #{content}, #{writer}, #{restaurantId})
            """)
    int insert(Review review);

    @Select("""
            SELECT no, title, writer, inserted
            FROM review
            ORDER BY no DESC
            """)
    List<Review> selectAll();

    @Select("""
            SELECT no, title, recommend, content, writer, inserted
            FROM review
            WHERE no = #{no}
            """)
    Review selectById(Integer no);

    @Delete("""
            DELETE FROM review
            WHERE no = #{no}
            """)
    int deleteById(Integer no);

    @Update("""
            UPDATE review
            SET title = #{title},
                recommend = #{recommend},
                content = #{content}
            WHERE no = #{no}
            """)
    void update(Review review);
}
