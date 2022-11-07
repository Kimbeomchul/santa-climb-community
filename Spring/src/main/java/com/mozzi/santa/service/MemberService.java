package com.mozzi.santa.service;

import com.mozzi.santa.dto.BugReport;
import com.mozzi.santa.dto.Member;

import java.util.List;

public interface MemberService {

    /**
     *  회원조회
     */
    Member selectMemberInfo(String socialId);

    /**
     *  회원번호 조회 ( 회원정보 조회용 )
     * */
    Long selectMemberId(String socialId);

    /**
     * 회원가입
     */
    Long signupMember(Member member);

    /**
     * 현재 접속중인 유저 정보 조회
     */
    List<Member> selectAllLoginUsers(List loginUsers, String socialId);

    // 멤버수정
    Long updateMemberInfo(Member member);

    /**
     * 팔로우기능
     */
    Long followMember(String mySocialId ,String socialId);

    /**
     * 팔로우 추가
     */
    Long follow(String mySocialId ,String socialId);

    /**
     * 팔로우 삭제
     */
    Long unfollow(String mySocialId ,String socialId);

    /**
     * 팔로워 조회
     */
    List<Member> selectFollowMembers(String socialId);

    /**
     * 팔로잉 조회
     */
    List<Member> selectFollowingMembers(String socialId);

    /**
     * 회원탈퇴
     */
    Long quitMember(String socialId);

    /**
     * 회원가입 username 중복확인
     */
    Long selectUsername(String username);

    /**
     * 버그리포트
     */
    Long insertBugReport(String socialId, String page, String detail);
    List<BugReport> selectBugReport(String socialId);

    /**
     * 게스트 수
     */
    Long selectGuestCnt();
}
