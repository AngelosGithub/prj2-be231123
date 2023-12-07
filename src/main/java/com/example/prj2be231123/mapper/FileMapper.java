package com.example.prj2be231123.mapper;

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
            SELECT fileName
            FROM reviewfile
            WHERE reviewId = #{reviewId}
            """)
    List<String> selectFilesByReviewId(Integer reviewId);
}
