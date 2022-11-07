package com.mozzi.santa.dto;

import lombok.Data;

@Data
public class Board {

    private Long boardNum;
    private String writer; // 글쓴이
    private String title; // 제목
    private String content; // 내용
    private String boardLikes; // 좋아요수
    private String boardComments; // 댓글
    private String boardImage; // 게시판이미지
    private String createDate; // 생성일
    private Long likesYsno; // 좋아요여부 ( 본인 )
    private String tag1; // 좋아요여부 ( 본인 )
    private String tag2; // 좋아요여부 ( 본인 )
    private String tag3; // 좋아요여부 ( 본인 )

    // 추천게시글용 멤버이미지
    private String memberImage;

}
