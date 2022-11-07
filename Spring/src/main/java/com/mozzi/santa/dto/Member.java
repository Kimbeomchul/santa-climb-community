package com.mozzi.santa.dto;

import lombok.Data;

@Data
public class Member {

    private Long mmbrNum;
    private Long boardCnt; // 게시글 수
    private Long followCnt; // 팔로워 수
    private String socialId; // 소셜아이디
    private String email; // 이메일
    private String phone; // 전화번호
    private String memberImage; // 프로필이미지
    private String socialType; // 소셜타입
    private String agreeYsno; // 동의여부
    private String nickname; // 닉네임
    private String grade; // 등급
    private String createdDate;

    /* FOLLOW */
    private String follower;
    private String following;

}
