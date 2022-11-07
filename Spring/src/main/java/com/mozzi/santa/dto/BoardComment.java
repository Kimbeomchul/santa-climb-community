package com.mozzi.santa.dto;

import lombok.Data;

@Data
public class BoardComment {

    private String commentsNum;
    private String boardNum;
    private String socialId;
    private String comments;
    private String createdDate;
    private String memberImage;
    private String nickname;
    private String reply;
    private String commentLikesYsno; // 개인 댓글좋아요여부
    private String commentLikesCnt;
    private String myCommentYsno;

}
