package com.mozzi.santa.controller.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozzi.santa.dto.modal.ChatData;
import com.mozzi.santa.dto.modal.ChatList;
import com.mozzi.santa.dto.modal.ChatRoom;
import com.mozzi.santa.service.socket.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.mozzi.santa.config.Constants.SESSION_NAME;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatService chatService;

    // 채팅 리스트
    @GetMapping("/list")
    public String chatting(@SessionAttribute(name= SESSION_NAME, required = false) String socialId,Model model){

        List<ChatList> memberRoom = chatService.findMemberRoom(socialId);
        log.debug("memberRoom {}", memberRoom);
        model.addAttribute("rooms", memberRoom);
        model.addAttribute("socialId", socialId);

        return "chat/chat_list";
    }


    // 현재회원 채팅방 목록반환
    @GetMapping("/rooms/{socialId}")
    @ResponseBody
    public List<ChatList> memberRoom(@PathVariable String socialId) {
        return chatService.findMemberRoom(socialId);
    }

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return chatService.findAllRoom();
    }

    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public String createRoom(String name, String socialId1 , String socialId2) {
        log.info("채팅방 개설됨 = 이름,소셜1,소셜2 = {}, {}, {} ", name, socialId1, socialId2);
        String checkRoom = chatService.checkMemberRoom(socialId1,socialId2);
        if(checkRoom != null){
            return checkRoom;
        }

        // 방 생성
        String createdChatRoom = chatService.createRoom(name, socialId1, socialId2);
        return createdChatRoom;
    }


    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/chat_detail";
    }

    // 채팅방정보얻기
    @GetMapping("/info/{roomId}")
    @ResponseBody
    public ChatData selectChatDataList(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String roomId){
        ChatData resChatData = chatService.selectChat(roomId);
        // 이게나야? 누가너야? 찾긔 호호
        if(resChatData.getOpponentUser().equals(socialId)) {
            String myImage = resChatData.getOpponentUserImage();
            String opponentImage = resChatData.getMyUserImage();
            String opponentId = resChatData.getOpponentUser();
            resChatData.setOpponentUserImage(opponentImage);
            resChatData.setOpponentUser(opponentId);
            resChatData.setMyUserImage(myImage);
        }
        return resChatData;
    }

    // 채팅방길이
    @GetMapping("/info/length/{roomId}")
    @ResponseBody
    public Long getChatLength(@SessionAttribute(name= SESSION_NAME, required = false) String socialId , @PathVariable String roomId){
        return chatService.getChatLength(roomId, socialId);
    }

    // 채팅방나가기
    @DeleteMapping("/out/{roomId}")
    @ResponseBody
    public Long rmChatRoom(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String roomId) {
        Long resRmChat = chatService.removeChatData(roomId,socialId);
        return resRmChat;
    }
}