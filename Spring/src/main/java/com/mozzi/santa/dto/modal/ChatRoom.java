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
public class ChatRoom{

    private String roomId;
    private String roomName;

    private String roomMember1;
    private String roomMember2;

    private Member member;

    public static ChatRoom create(String name, String roomMember1, String roomMember2) {
        ChatRoom room = new ChatRoom();
        room.roomId = UUID.randomUUID().toString();
        room.roomName = name;
        room.roomMember1 = roomMember1;
        room.roomMember2 = roomMember2;
        return room;
    }
}