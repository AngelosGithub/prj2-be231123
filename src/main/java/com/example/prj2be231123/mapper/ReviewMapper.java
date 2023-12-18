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
    @Options(useGeneratedKeys = true, keyProperty = "no")
    // 파일 첨부할 글의 번호를 알아냄
    int insert(Review review);

    @Select("""
            SELECT r.no,
                   r.title,
                   re.place,
                   m.nickName,
                   r.writer,
                   r.inserted,
                   st.point starPoint,
                   COUNT(c.no) countComment
            FROM review r JOIN member m ON r.writer = m.id
                          LEFT JOIN comment c ON r.no = c.reviewId
                          JOIN starpoint st ON r.no = st.reviewId
                          LEFT JOIN restaurant re ON r.restaurantId = re.no
            WHERE r.content LIKE #{keyword}
               OR r.title LIKE #{keyword}
            GROUP BY r.no
            ORDER BY r.no DESC
            LIMIT #{from}, 9;
            """)
    List<Review> selectAll(Integer from, String keyword);

    @Select("""
            SELECT r.no,
                   r.title,
                   r.recommend,
                   r.content,
                   r.writer,
                   m.nickName,
                   r.inserted,
                   re.place,
                   st.point starPoint
            FROM review r JOIN member m ON r.writer = m.id
                          JOIN starpoint st ON r.no = st.reviewId
                          JOIN restaurant re ON r.restaurantId = re.no
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


    @Select("""
             SELECT  rv.no,
                     rv.title,
                     rv.recommend,
                     rv.content,
                     rv.writer,
                     rv.inserted,
                     st.point starPoint
             FROM review rv LEFT JOIN starpoint st
                     ON rv.no = st.reviewId
             WHERE restaurantId = #{restaurantId}
             ORDER BY rv.no DESC
             LIMIT 3;
            """)
    List<Review> selectByRestaurant(Integer restaurantId);


    @Select("""
                SELECT no
                FROM review
                WHERE restaurantId = #{restaurantId}
            """)
    List<Integer> selectListByRestaurantN(Integer no);

    @Select("""
            SELECT no
            FROM review
            WHERE writer = #{id}
            """)
    List<Integer> selectIdListByMemberId(String writer);

    @Select("""
            SELECT COUNT(*) FROM review
            WHERE title LIKE #{keyword}
               OR content LIKE #{keyword};
            """)
    int allPages(String keyword);


    @Select("""
                SELECT no
                FROM review
                WHERE restaurantId = #{restaurantId}
            """)
    List<Integer> selectListByRestaurantNo(Integer no);

    @Select("""
            SELECT r.no,
                   r.title,
                   re.place,
                   m.nickName,
                   r.writer,
                   r.inserted,
                   r.restaurantId,
                   st.point starPoint,
                   COUNT(c.no) countComment
            FROM review r JOIN member m ON r.writer = m.id
                          LEFT JOIN comment c ON r.no = c.reviewId
                          LEFT JOIN starpoint st ON st.reviewId = r.no
                          LEFT JOIN restaurant re ON r.restaurantId = re.no
            WHERE restaurantId = #{restaurantId}
            GROUP BY r.no
            ORDER BY r.no DESC
            LIMIT #{from}, 9;
            """)
    List<Review> selectByRestaurantNo(Integer from, Integer restaurantId);
}
