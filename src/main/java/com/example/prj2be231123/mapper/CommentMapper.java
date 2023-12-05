package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("""
            INSERT INTO comment (reviewId, comment, memberId)
            VALUES (#{reviewId}, #{comment}, #{memberId})
            """)
    int insert(Comment comment);

    @Select("""
            SELECT *
            FROM comment
            WHERE reviewId = #{reviewId}
            """)
    List<Comment> selectByReviewId(Integer reviewId);

    @Delete("""
            DELETE FROM comment
            WHERE no = #{no}
            """)
    int deleteById(Integer no);

    @Select("""
            SELECT * FROM comment
            WHERE no = #{no}
            """)
    Comment selectById(Integer no);

    @Update("""
            UPDATE comment
            SET comment = #{comment}
            WHERE no = #{no}
            """)
    int update(Comment comment);
}
