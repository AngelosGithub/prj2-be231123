package com.example.prj2be231123.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StarMapper {

    @Insert("""
            INSERT INTO starpoint(memberId, reviewId, point)
            VALUES (#{memberId}, #{reviewId}, #{point})
            """)
    int insert(Integer reviewId, String memberId, int point);

    @Delete("""
            DELETE FROM starpoint
            WHERE reviewId = #{reviewId}
            """)
    int deleteByReviewId(Integer reviewId);
}
