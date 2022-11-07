package com.mozzi.santa.controller.socket;

import com.mozzi.santa.dto.modal.ChatMessage;
import com.mozzi.santa.service.socket.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void enter(ChatMessage message) {
/*        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender()+"님이 입장하였습니다.");
        }*/
        if(ChatMessage.MessageType.QUIT.equals(message.getType())){
            String modifyChatData = message.getChatData().replaceAll("\\[","").replaceAll("\\]","");
            modifyChatData = "[" + modifyChatData + "]";
            chatService.saveChat(message.getMyUser(), message.getMyUserImage(), message.getOpponentUser(), modifyChatData , message.getRoomId(), message.getChatDataLength());
        }else{
            sendingOperations.convertAndSend("/topic/chat/room/"+message.getRoomId(),message);
        }
    }


}