package com.mozzi.santa.config.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {


    /**
     * 해당 핸들러를 통해 채팅방 접속 / 퇴장 / 보는중을 체크함
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT == accessor.getCommand()){
            // 접근시 뭘하면좋을까 ?_ ?
        }else if (StompCommand.DISCONNECT == accessor.getCommand()){
            // 채팅방 나가기
        }else if (StompCommand.SUBSCRIBE == accessor.getCommand()){
            // todo : 1표시후 상대방이 채팅 읽었는지 기록남겨주는 기능
        }

        return message;
    }

}
