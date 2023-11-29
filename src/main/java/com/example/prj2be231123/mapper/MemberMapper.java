package com.example.prj2be231123.mapper;

import com.example.prj2be231123.domain.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberMapper {

    @Insert("""
            INSERT INTO member (id, password, gender, phone, email, birthDate)
            VALUES (#{id}, #{password}, #{gender}, #{phone}, #{email}, #{birthDate})
            """)
    int insert(Member member);

    @Select("""
            SELECT id FROM member
            WHERE id = #{id}
            """)
    String selectId(String id);

    @Select("""
            SELECT *
            FROM member
            WHERE id = #{id}
            """)
    Member selectById(String id);

    @Select("""
            SELECT id, nickName, email, inserted
            FROM member
            ORDER BY inserted DESC
            """)
    List<Member> selectAll();
}
