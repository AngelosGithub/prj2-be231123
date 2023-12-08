package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.ReviewFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper {

    @Insert("""
            INSERT INTO reviewfile (reviewId, fileName)
            VALUES (#{reviewId}, #{fileName})
            """)
    int insert(Integer reviewId, String fileName);

    @Select("""
            SELECT no, fileName
            FROM reviewfile
            WHERE reviewId = #{reviewId}
            """)
    List<ReviewFile> selectFilesByReviewId(Integer reviewId);

    @Delete("""
            DELETE FROM reviewfile
            WHERE reviewId = #{reviewId}
            """)
    int deleteByReviewId(Integer reviewId);
}
