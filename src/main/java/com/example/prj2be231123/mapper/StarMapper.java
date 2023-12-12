package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Star;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

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

    @Update("""
            UPDATE starpoint
            SET point = #{point}
            WHERE reviewId = #{reviewId}
            """)
    int update(Integer point, Integer reviewId);
}
