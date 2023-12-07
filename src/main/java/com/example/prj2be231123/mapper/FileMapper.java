package com.example.prj2be231123.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

    @Insert("""
            INSERT INTO reviewfile (reviewId, fileName)
            VALUES (#{reviewId}, #{fileName})
            """)
    int insert(Integer reviewId, String fileName);
}
