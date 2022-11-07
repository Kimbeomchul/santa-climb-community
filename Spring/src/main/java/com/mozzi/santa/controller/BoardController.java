package com.mozzi.santa.controller;

import com.mozzi.santa.config.SessionListener;
import com.mozzi.santa.dto.Board;
import com.mozzi.santa.dto.BoardComment;
import com.mozzi.santa.dto.BoardDetail;
import com.mozzi.santa.dto.Mountain;
import com.mozzi.santa.service.BoardService;
import com.mozzi.santa.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mozzi.santa.config.Constants.SESSION_NAME;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final SessionListener sessionListener;

    // 게시판 리스트 Default : latest
    @GetMapping("/list")
    public String boardList(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, Model model, @RequestParam(defaultValue = "1") String options){
        List<Board> boardList = boardService.selectBoardList(0,20 , socialId , options); // start, end
        List<Board> recBoard = boardService.selectRecommendBoardList(socialId);
        log.debug("boardList = {} , recBoard = {} ", boardList , recBoard);

        if(!socialId.replaceAll("-","").equals(socialId)){
            model.addAttribute("guest", "Y");
        }else{
            model.addAttribute("guest", "N");
        }

        model.addAttribute("options", options);
        model.addAttribute("recommend", recBoard);
        model.addAttribute("board" , boardList);
        return "board/board_list";
    }

    // 게시판 페이징 최신순
    @RequestMapping("/list/{start}")
    @ResponseBody
    public List<Board> pagingBoard(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable long start,  @RequestParam(defaultValue = "1") String options){
        List<Board> boardList = boardService.selectBoardList(start,start + 20 , socialId ,options);
        log.debug("pagingBoard = {}" , boardList);

        return boardList;
    }


    // 게시판 페이징 인기순
    @RequestMapping("/list/popular/{start}")
    @ResponseBody
    public List<Board> pagingPopularBoard(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable long start,  @RequestParam(defaultValue = "1") String options){
        List<Board> boardList = boardService.selectBoardListPopular(start,start + 20 , socialId ,options);
        log.debug("pagingPopularBoard = {}" , boardList);

        return boardList;
    }

    @GetMapping("/detail/{boardNum}")
    public String boardDetail(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String boardNum, Model model){
        // Guest 체크
        if(!socialId.replaceAll("-","").equals(socialId)){
            return "redirect:https://santa-community.co.kr/board/list?guest=1";
        }
        BoardDetail boardDetail = boardService.selectBoardDetail(socialId,boardNum);
        List<BoardComment> boardComment = boardService.selectBoardComments(socialId, boardNum);
        List loginUsers = sessionListener.getLoginList();

        if(boardDetail.getSocialId().equals(socialId)){
            model.addAttribute("myBoard" , "Y");
        }else{
            model.addAttribute("myBoard" , "N");
        }
        log.debug("boardDetail = {}" , boardDetail);

        model.addAttribute("boardNum",boardNum);
        model.addAttribute("board" , boardDetail);
        model.addAttribute("comments" , boardComment);
        model.addAttribute("loginUsers", loginUsers);
        model.addAttribute("commentCnt" , boardComment.size());

        return "board/board_detail";
    }

    @PostMapping("/detail/{boardNum}/comment/write")
    public String boardComment(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @RequestParam String comments , @RequestParam("reply") String reply , @PathVariable String boardNum ){
        if(comments.trim().equals("")){
            return "redirect:/board/detail/"+ boardNum;
        }

        boardService.insertComments(boardNum,socialId,comments ,reply);

        return "redirect:/board/detail/"+ boardNum;
    }

    @GetMapping("/write")
    public String boardWrite(){
        return "board/board_write";
    }

    @GetMapping("/edit")
    public String boardEdit(@SessionAttribute(name= SESSION_NAME, required = false) String socialId , Model model, Board board){
        String writer = boardService.selectBoardWriter(board.getBoardNum().toString());
        if(!socialId.equals(writer)){
            log.info("비정상적인 접근 : 허가되지 않은 사용자가 게시판 Edit 실행 = 현재로그인 : {} , 허가자 : {} ",socialId , writer);
            return "redirect:/board/list";
        }
        model.addAttribute("board", board);
        return "board/board_edit";
    }
    @PostMapping("/write")
    public String boardUpload(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, Board board){
        log.debug("boardUpload = {}" , board);
        if(board.getContent() != null && board.getContent() != ""){
            String contents = (board.getContent()).replace("\r\n","<br>");
            board.setContent(contents);
        }

        boardService.insertBoard(socialId, board);
        return "redirect:https://santa-community.co.kr/board/list?motion=Y";
    }

    @PostMapping("/edit/{boardNum}")
    public String boardUpdate(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, Board board ,@PathVariable String boardNum){
        String writer = boardService.selectBoardWriter(board.getBoardNum().toString());
        if(!socialId.equals(writer)){
            log.info("비정상적인 접근 : 허가되지 않은 사용자가 게시판 Edit 실행 = 현재로그인 : {} , 허가자 : {} ",socialId , writer);
            return "redirect:/board/list";
        }
        log.debug("updateBoard = {}" , board);
        boardService.updateBoard(board , boardNum);

        return "redirect:https://santa-community.co.kr/board/list";
    }



    @PostMapping("/like/{boardNum}")
    @ResponseBody
    public Long boardLikes(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String boardNum){
        Long resLikes = boardService.updateLikes(socialId,boardNum);
        // 2L 삽입 3L 삭제
        if(resLikes == 0){
            log.info("############### 좋아요 에러 /like/{boardNum}");
        }

        return resLikes;
    }

    @DeleteMapping("/detail/{boardNum}/remove")
    @ResponseBody
    public Long removeBoard(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String boardNum){
        String writer = boardService.selectBoardWriter(boardNum);
        if(!socialId.equals(writer)){
            log.info("비정상적인 접근 : 허가되지 않은 사용자가 게시판 Delete 실행 = 현재로그인 : {} , 허가자 : {} ",socialId , writer);
            return -1L;
        }
        Long resDelete = boardService.deleteBoard(boardNum);
        return resDelete;
    }


    // 검색기능
    @GetMapping("/search/{searchWord}")
    public String searchBoard(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String searchWord,Model model){
        List<Board> boardList = boardService.selectSearchedBoardList(socialId, searchWord);
        log.debug("searchBoard = {}" , boardList);

        model.addAttribute("search", searchWord);
        if(boardList.size() == 0){
            return "board/board_search_empty";
        }

        model.addAttribute("board", boardList);
        return "board/board_search";
    }


    // 프로필 게시판 검색기능
    @GetMapping("/search/profile/{socialId}/{writer}")
    public String searchProfileBoard(@SessionAttribute(name= SESSION_NAME, required = false) String mySocialId, @PathVariable String socialId,@PathVariable String writer, Model model){
        List<Board> boardList = boardService.selectSearchProfileBoardList(mySocialId, socialId);
        log.debug("searchProfileBoard = {}" , boardList);

        model.addAttribute("writer",writer);
        model.addAttribute("search", "fromProfile");
        model.addAttribute("board", boardList);
        return "board/board_search";
    }


    @PostMapping("/comment/like/{commentNum}")
    @ResponseBody
    public Long commentLikes(@SessionAttribute(name= SESSION_NAME, required = false) String socialId , @PathVariable String commentNum){
        Long resLikes = boardService.updateCommentLikes(socialId,commentNum);
        // 2L 삽입 3L 삭제
        if(resLikes == 0){
            log.info("############### 댓글 좋아요에러 = {} {}" , socialId,commentNum);
        }

        return resLikes;
    }


    // 댓글 삭제
    @DeleteMapping("/comment/{commentsNum}")
    @ResponseBody
    public Long deleteComment(@SessionAttribute(name= SESSION_NAME, required = false) String socialId , @PathVariable String commentsNum){
        Long resDelete = boardService.deleteComment(socialId, commentsNum);
        return resDelete;
    }

    // 유저 벤기능
    @PostMapping("/ban/{banSocialId}")
    @ResponseBody
    public Long banUser(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String banSocialId){

        if(socialId.equals(banSocialId)){
            log.info("########## 유저 벤기능 , 자기자신을 벤 시도 = {} ", socialId);
            return 0L;
        }

        Long resBan = boardService.insertBanUser(socialId,banSocialId);

        return resBan;
    }

    // 유저 신고기능
    @PostMapping("/report/{flag}/{domNum}")
    @ResponseBody
    public Long reportUser(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String flag, @PathVariable String domNum){

        Long resReport = boardService.findReport(socialId, flag, domNum);
        return resReport;
    }
}
