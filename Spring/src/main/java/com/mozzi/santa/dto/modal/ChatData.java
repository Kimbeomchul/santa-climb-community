package com.mozzi.santa.dto.modal;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatData {

    //채팅방 ID
    private String roomId;
    // 상대방
    private String opponentUser;
    // 상대방이미지
    private String opponentUserImage;
    // 전체데이터
    private String chatData;
    // 나
    private String myUser;
    // 내이미지
    private String myUserImage;

}