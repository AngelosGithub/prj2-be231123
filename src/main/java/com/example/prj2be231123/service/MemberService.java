package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Auth;
import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.mapper.CommentMapper;
import com.example.prj2be231123.mapper.MemberMapper;
import com.example.prj2be231123.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MemberService {
    private final MemberMapper mapper;
    private final ReviewMapper reviewMapper;
    private final CommentMapper commentMapper;
    private final ReviewService reviewService;


    public boolean add(Member member) {
        return mapper.insert(member) == 1;
    }

    public boolean validate(Member member) {
        if (member == null) {
            return false;
        }

        if (member.getId().isBlank()) {
            return false;
        }

        if (member.getPassword().isBlank()) {
            return false;
        }

        if (member.getEmail().isBlank()) {
            return false;
        }

        if (member.getGender().isBlank()) {
            return false;
        }

        if (member.getPhone().isBlank()) {
            return false;
        }

        if (member.getBirthDate().isBlank()) {
            return false;
        }
        return true;
    }

    public String getId(String id) {
        return mapper.selectId(id);
    }

    public boolean login(Member member, WebRequest request) {
        Member dbMember = mapper.selectById(member.getId());


        if (dbMember != null) {
            if (dbMember.getPassword().equals(member.getPassword())) {

                // 멤버 아이디로 권한정보를 얻어서 리스트로 받은 뒤
                List<Auth> auth = mapper.selectAuthId(member.getId());
                // 값을 추가
                dbMember.setAuth(auth);

                dbMember.setPassword("");
                request.setAttribute("login", dbMember, RequestAttributes.SCOPE_SESSION);
                return true;
            }
        }
        return false;
    }

    public Member getMember(String id) {
        return mapper.selectById(id);
    }

    public List<Member> list() {
        return mapper.selectAll();
    }

    public boolean deleteMember(String id) {
        // 이 멤버가 작성한 댓글 삭제
        commentMapper.deleteByMemberId(id);

        // 이 멤버가 작성한 게시물 삭제
        // 1.삭제시 게시물 번호를 조회하여
        List<Integer> reviewIdList = reviewMapper.selectIdListByMemberId(id);
        // 2.번호를 loop 사용하여 다른사람이 작성한 댓글도 삭제(reviewService.remove 사용)
        reviewIdList.forEach((reviewId) -> reviewService.remove(reviewId));

//        reviewMapper.deleteByWriter(id);
//        기존 코드 삭제

        // 멤버 삭제
        return mapper.deleteById(id) == 1;
    }

    public String getNickName(String nickName) {
        return mapper.selectByNick(nickName);
    }

    public boolean update(Member member) {
//        Member oldMember = mapper.selectById(member.getId());
//
//        if (member.getPassword().equals("")) {
//            member.getPassword(oldMember.getPassword());
//        }

        return mapper.update(member) == 1;
    }

    public boolean hasAccess(String id, Member login) {
        if (isAdmin(login)) {
            return true;
        }

        return login.getId().equals(id);
    }

    public boolean isAdmin(Member login) {
        if (login.getAuth() != null) {
            return login.getAuth()
                    .stream()
                    .map(e -> e.getManager())
                    .anyMatch(n -> n.equals("admin"));
        }
        return false;
    }
}
