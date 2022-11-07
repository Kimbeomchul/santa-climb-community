package com.mozzi.santa.service.socket;

import com.mozzi.santa.dto.Member;
import com.mozzi.santa.dto.modal.ChatData;
import com.mozzi.santa.dto.modal.ChatList;
import com.mozzi.santa.dto.modal.ChatRoom;
import com.mozzi.santa.mapper.ChatMapper;
import com.mozzi.santa.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final MemberService memberService;
    private final ChatMapper chatMapper;

    //채팅방 불러오기
    public List<ChatRoom> findAllRoom() {
        //채팅방 최근 생성 순으로 반환
        List<ChatRoom> result = chatMapper.selectAllChatRooms();
        return result;
    }

    //채팅방 생성
    public String createRoom(String name ,String member1 , String member2) {
        String chatUUID = UUID.randomUUID().toString();
        chatMapper.insertChatRoom(member1, member2, chatUUID);

        String checkRoomId = this.checkMemberRoom(member1,member2);
        return checkRoomId;
    }

    //상대방 체크 ( 이미 방이 존재하는지 확인 )
    public String checkMemberRoom(String socialId1, String socialId2){
        String roomId = chatMapper.findChatRoomId(socialId1, socialId2);
        return roomId;
    }

    //채팅방 불러오기
    public List<ChatList> findMemberRoom(String socialId) {
        List<ChatRoom> chatRooms = chatMapper.findChatRoom(socialId);
        List<ChatList> resRoomList = new ArrayList<>();

        for(int i=0; i<chatRooms.size(); i++){
            Member resOpponent = new Member();
            Member resMe = new Member();
            ChatList resList = new ChatList();

            // member1이 나일경우
            if(chatRooms.get(i).getRoomMember1().equals(socialId)){
                resMe = memberService.selectMemberInfo(socialId);
                resOpponent = memberService.selectMemberInfo(chatRooms.get(i).getRoomMember2());

            }else if(chatRooms.get(i).getRoomMember2().equals(socialId)){
                resMe = memberService.selectMemberInfo(socialId);
                resOpponent = memberService.selectMemberInfo(chatRooms.get(i).getRoomMember1());

            }
            // 채팅방 set
            resList.setChatRoom(chatRooms.get(i));
            // 나 set
            resList.setMe(resMe);
            // 상대 set
            resList.setOpponent(resOpponent);
            resRoomList.add(resList);
        }
        return resRoomList;
    }


    // 채팅방 저장
    public Long saveChat(String me, String myImage , String opponent , String chatData , String roomId, String chatDataLength){
        log.debug("########### CHAT DATA SAVE #############");
        Long resChatSave = 0L;
        Long getLength = this.getChatLength(roomId,me);
        ChatData resData = this.selectChat(roomId);

        if(getLength == null){
            chatMapper.saveChatLength(roomId,me,chatDataLength);
        }else{
            chatMapper.updateChatLength(roomId,me,chatDataLength);
        }

        if(resData != null){
            resChatSave = chatMapper.updateChat(me,myImage,opponent,chatData,roomId);
        }else{
            resChatSave = chatMapper.saveChat(me,myImage,opponent,chatData,roomId);
        }
        return resChatSave;
    }
    public Long getChatLength(String roomId, String me){
        Long resGetChatDataLength = chatMapper.getChatLength(roomId, me);
        return resGetChatDataLength;
    }
    // 채팅방 불러오기
    public ChatData selectChat(String roomId){
        ChatData resGetChat = chatMapper.selectChat(roomId);
        return resGetChat;
    }

    // 채팅방나가기
    @Transactional
    public Long removeChatData(String roomId, String socialId){
        try{
            Long chatRoom = this.removeChatRoom(roomId,socialId);
            Long MsgData = this.removeMsgData(roomId);
            Long MsgCounter = this.removeMsgCounter(roomId, socialId);
            if(chatRoom != 0 && MsgData != 0 && MsgCounter != 0) {
                return 1L;
            }
        }catch (Exception e){
            log.info("########## 채팅방 나가기 로직 실패 = {} ", e);
        }
        return 0L;
    }

    /**
     * 채팅방 삭제 로직 세개
     * 1. 채팅방 삭제
     * 2. 채팅방 내용삭제
     * 3. 채팅방 카운터삭제
     * 순서대로
     */
    public Long removeChatRoom(String roomId, String socialId){
        return chatMapper.removeChatRoom(roomId, socialId);
    }
    public Long removeMsgData(String roomId){
        return chatMapper.removeMsgData(roomId);
    }
    public Long removeMsgCounter(String roomId, String socialId){
        return chatMapper.removeMsgCounter(roomId, socialId);
    }
}