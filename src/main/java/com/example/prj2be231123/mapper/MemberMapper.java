package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    @Insert("""
            INSERT INTO member (id, password, gender, phone, email, birthDate)
            VALUES (#{id}, #{password}, #{gender}, #{phone}, #{email}, #{birthDate})
            """)
    int insert(Member member);
}
