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
            SELECT r.no, r.title, m.nickName, r.writer, r.inserted
            FROM review r JOIN member m ON r.writer = m.id
            ORDER BY r.no DESC
            """)
    List<Review> selectAll();

    @Select("""
            SELECT r.no, r.title, r.recommend, r.content, r.writer, m.nickName, r.inserted
            FROM review r JOIN member m ON r.writer = m.id
            WHERE r.no = #{no}
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
    int update(Review review);

    @Delete("""
            DELETE FROM review
            WHERE writer = #{writer}
            """)
    int deleteByWriter(String id);
}
