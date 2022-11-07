package com.mozzi.santa.dto.modal;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK, QUIT
    }

    private MessageType type;
    //채팅방 ID
    private String roomId;
    //전송자
    private String sender;
    //내용
    private String message;
    //전송자 사진
    private String senderImage;


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
    // 길이
    private String chatDataLength;
}