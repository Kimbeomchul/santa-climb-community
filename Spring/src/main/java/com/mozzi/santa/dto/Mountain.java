package com.mozzi.santa.dto;

import lombok.Data;

@Data
public class Mountain {

    private Long mntcode; // auto inc
    private Long mntlikes; // 산 좋아요
    private String mntidetails; // 산 설명
    private String mntimages; // 산 이미지
    private String mntiadminnum; // 관리자번호
    private String mntihigh; // 높이
    private String mntiadmin; // 관리자
    private String mntilistno; // 산번호
    private String mntiadd; // 산 위치
    private String mntiname; // 산 이름
    private Long likesYsno; // 산 좋아요 YSNO
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;

    /* 산위치 정보 */
    private String mntilat;
    private String mntilon;

    /* 추가요청 */
    private String requestType;

}
