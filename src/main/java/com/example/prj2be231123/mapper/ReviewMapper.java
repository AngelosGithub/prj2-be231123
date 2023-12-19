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
            <script>
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
            WHERE
                <trim prefixOverrides="OR">
                    <if test="category == 'all' or category == 'title'">
                        OR r.title LIKE #{keyword}
                        OR re.place LIKE #{keyword}
                    </if>
                    <if test="category == 'all' or category == 'content'">
                        OR r.content LIKE #{keyword}
                    </if>
                </trim>
            GROUP BY r.no
            ORDER BY r.no DESC
            LIMIT #{from}, 6;
            </script>
            """)
    List<Review> selectAll(Integer from, String keyword, String category);

    @Select("""
            SELECT r.no,
                   r.title,
                   r.recommend,
                   r.content,
                   r.writer,
                   m.nickName,
                   r.inserted,
                   re.place,
                   r.restaurantId,
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
            <script>
            SELECT COUNT(*)
            FROM review r LEFT JOIN restaurant re ON r.restaurantId = re.no
            WHERE
                <trim prefixOverrides="OR">
                    <if test="category == 'all' or category == 'title'">
                        OR r.title LIKE #{keyword}
                        OR re.place LIKE #{keyword}
                    </if>
                    <if test="category == 'all' or category == 'content'">
                        OR r.content LIKE #{keyword}
                    </if>
                </trim>
            </script>
            """)
    int allPages(String keyword, String category);


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

    @Select("""
                SELECT COUNT(rf.no)
                FROM review r LEFT JOIN reviewfile rf
                    ON r.no = rf.reviewId
                WHERE reviewId= #{reviewId}
            """)
    Integer selectFileCount(Integer no);
}
