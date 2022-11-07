package com.mozzi.santa.service;

import com.mozzi.santa.dto.BugReport;
import com.mozzi.santa.dto.Member;
import com.mozzi.santa.dto.tempDTO;
import com.mozzi.santa.mapper.MemberMapper;
import com.mozzi.santa.mapper.MountainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;


    @Override
    public Member selectMemberInfo(String socialId) {
        return memberMapper.selectMemberInfo(socialId);
    }

    @Override
    public Long selectMemberId(String socialId) {
        return memberMapper.selectMemberId(socialId);
    }

    @Override
    public Long signupMember(Member member) {
        return memberMapper.signupMember(member);
    }

    @Override
    public List<Member> selectAllLoginUsers(List loginUsers , String socialId) {
        List<Member> resLoginUsers = new ArrayList<>();

        for (Object loginUser : loginUsers){
            if(!loginUser.equals(socialId)) {
                resLoginUsers.add(this.selectMemberInfo((String) loginUser));
            }
        }

        return resLoginUsers;
    }

    @Override
    public Long updateMemberInfo(Member member) {
        return memberMapper.updateMemberInfo(member);
    }


    // 팔로우기능
    @Override
    public Long followMember(String mySocialId, String socialId) {
        Long isFollowing = memberMapper.followMember(mySocialId, socialId);
        Long resFollow = 0L;
        if(isFollowing == 0){
            resFollow = this.follow(mySocialId, socialId);
        }else{
            resFollow = this.unfollow(mySocialId, socialId);
        }
        return resFollow;
    }

    @Override
    public Long follow(String mySocialId, String socialId) {
        return memberMapper.follow(mySocialId, socialId);
    }

    @Override
    public Long unfollow(String mySocialId, String socialId) {
        return memberMapper.unfollow(mySocialId, socialId);
    }

    @Override
    public List<Member> selectFollowMembers(String socialId) {
        return memberMapper.selectFollowMembers(socialId);
    }

    @Override
    public List<Member> selectFollowingMembers(String socialId) {
        return memberMapper.selectFollowingMembers(socialId);
    }

    @Override
    @Transactional
    public Long quitMember(String socialId) {
        try{
            memberMapper.quitFollow(socialId);
            memberMapper.quitBoard(socialId);
            memberMapper.quitBoardComments(socialId);
            memberMapper.quitBoardLikes(socialId);
            memberMapper.quitMtLikes(socialId);
            memberMapper.quitMember(socialId);
        }catch (Exception e){
            log.error("######## 회원탈퇴 실패 = {}", e);
        }

        return 0L;
    }

    @Override
    public Long selectUsername(String username) {
        return memberMapper.selectUsername(username);
    }

    @Override
    public Long insertBugReport(String socialId, String page, String detail) {
        return memberMapper.insertBugReport(socialId, page, detail);
    }

    @Override
    public List<BugReport> selectBugReport(String socialId) {
        return memberMapper.selectBugReport(socialId);
    }

    @Override
    public Long selectGuestCnt() {
        return memberMapper.selectGuestCnt();
    }
}
