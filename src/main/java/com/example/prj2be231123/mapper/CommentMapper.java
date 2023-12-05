package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
