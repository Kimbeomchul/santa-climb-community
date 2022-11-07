package com.mozzi.santa.dto.modal;

import com.mozzi.santa.dto.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatList {

    private ChatRoom chatRoom;
    private Member opponent;
    private Member me;
}