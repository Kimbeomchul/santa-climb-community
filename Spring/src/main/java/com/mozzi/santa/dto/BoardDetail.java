package com.mozzi.santa.dto;

import lombok.Data;

@Data
public class BoardDetail {

    private Long boardNum;
    private String socialId;
    private String title;
    private String content;
    private String boardImage;
    private String tag1;
    private String tag2;
    private String tag3;
    private String writer;
    private String writerImage;
    private String boardLikes;
    private String boardComments;
    private String userLikes;
    private String createDate;
    private String followYsno;

}
