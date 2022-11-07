package com.mozzi.santa.service;

import com.mozzi.santa.dto.Board;
import com.mozzi.santa.dto.BoardComment;
import com.mozzi.santa.dto.BoardDetail;
import com.mozzi.santa.dto.Member;
import com.mozzi.santa.mapper.BoardMapper;
import com.mozzi.santa.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;


    @Override
    public List<Board> selectBoardList(long start, long end , String socialId ,String options) {
        // 최신순
        if(options.equals("1")){
            return boardMapper.selectBoardList(start, end , socialId ,options);
        }else{ // 인기순
            return this.selectBoardListPopular(start, end, socialId, options);
        }
    }

    @Override
    public List<Board> selectBoardListPopular(long start, long end , String socialId ,String options) {
        return boardMapper.selectBoardListPopular(start, end , socialId ,options);
    }

    @Override
    public BoardDetail selectBoardDetail(String socialId, String boardNum) {
        return boardMapper.selectBoardDetail(socialId ,boardNum);
    }

    @Override
    public Long updateLikes(String socialId, String boardNum) {
        Long findLikeYsno = this.findBoardLikes(socialId, boardNum);
        Long successYsno = 0L;

        // 좋아요상태가 아닐시
        if(findLikeYsno == 0){
            this.insertLikes(socialId, boardNum);
            successYsno = 2L;
        }else{
            this.deleteLikes(socialId, boardNum);
            successYsno = 3L;
        }

        return successYsno;
    }

    @Override
    public Long insertLikes(String socialId, String boardNum) {
        return boardMapper.insertLikes(socialId, boardNum);
    }

    @Override
    public Long deleteLikes(String socialId, String boardNum) {
        return boardMapper.deleteLikes(socialId, boardNum);
    }

    @Override
    public List<BoardComment> selectBoardComments(String socialId, String boardNum) {
        List<BoardComment> imsiComment = boardMapper.selectBoardComments(socialId, boardNum);
        List<BoardComment> replyComment = new ArrayList<>();
        List<BoardComment> resComment = new ArrayList<>();

        //답글 담기
        for(BoardComment comments : imsiComment){
            // 답글 존재시
            if(!comments.getReply().equals("")){
                replyComment.add(comments);
            }
        }

        for(int i=0; i<imsiComment.size(); i++){
            if(imsiComment.get(i).getReply().equals("")){
                resComment.add(imsiComment.get(i));
            }
            // 답글을 돌며 답글저장
            for(int j=0; j<replyComment.size(); j++){
                if(imsiComment.get(i).getCommentsNum().equals(replyComment.get(j).getReply())){
                    resComment.add(replyComment.get(j));
                }
            }
        }
        return resComment;
    }

    @Override
    public Long insertComments(String boardNum, String socialId, String comments ,String reply) {
        return boardMapper.insertComments(boardNum, socialId, comments, reply);
    }


    // 게시판 삭제
    @Override
    public Long deleteBoard(String boardNum) {
        boardMapper.deleteLikesFordel(boardNum);
        boardMapper.deleteCommentsFordel(boardNum);
        return boardMapper.deleteBoard(boardNum);
    }


    @Override
    public List<Board> selectRecommendBoardList(String socialId) {
        return boardMapper.selectRecommendBoardList(socialId);
    }

    @Override
    public List<Board> selectSearchedBoardList(String socialId, String searchWord) {
        return boardMapper.selectSearchedBoardList(socialId, searchWord);
    }

    @Override
    public List<Board> selectSearchProfileBoardList(String mySocialId, String socialId) {
        return boardMapper.selectSearchProfileBoardList(mySocialId, socialId);
    }

    @Override
    public Long insertBoard(String socialId, Board board) {
        return boardMapper.insertBoard(socialId, board);
    }

    @Override
    public Long updateBoard(Board board, String boardNum) {
        return boardMapper.updateBoard(board, boardNum);
    }

    @Override
    public Long findCommentLikes(String socialId, String commentNum) {
        return boardMapper.findCommentLikes(socialId, commentNum);
    }

    @Override
    public Long updateCommentLikes(String socialId, String commentNum) {
        Long findCommentLikeYsno = this.findCommentLikes(socialId, commentNum);
        Long successYsno = 0L;

        // 좋아요상태가 아닐시
        if(findCommentLikeYsno == 0){
            this.insertCommentLikes(socialId, commentNum);
            successYsno = 2L;
        }else{
            this.deleteCommentLikes(socialId, commentNum);
            successYsno = 3L;
        }

        return successYsno;
    }

    @Override
    public Long insertCommentLikes(String socialId, String commentNum) {
        return boardMapper.insertCommentLikes(socialId, commentNum);
    }

    @Override
    public Long deleteCommentLikes(String socialId, String commentNum) {
        return boardMapper.deleteCommentLikes(socialId, commentNum);
    }

    // 댓글삭제
    @Override
    public Long deleteComment(String socialId, String commentsNum) {
        // 답글 전체삭제
        this.deleteAllReply(socialId, commentsNum);
        // 답글 좋아요삭제
        this.deleteReplyLikes(socialId, commentsNum);

        return boardMapper.deleteComment(socialId, commentsNum);
    }

    // 답글 전체삭제
    @Override
    public Long deleteAllReply(String socialId, String commentsNum) {
        return boardMapper.deleteAllReply(socialId, commentsNum);
    }
    // 답글 좋아요삭제
    @Override
    public Long deleteReplyLikes(String socialId, String commentsNum) {
        return boardMapper.deleteReplyLikes(socialId, commentsNum);
    }

    @Override
    public String selectBoardWriter(String boardNum) {
        return boardMapper.selectBoardWriter(boardNum);
    }

    @Override
    public Long insertBanUser(String socialId, String banSocialId) {
        return boardMapper.insertBanUser(socialId, banSocialId);
    }

    // 게시판의 좋아요 여부 판단
    @Override
    public Long findBoardLikes(String socialId, String boardNum) {
        return boardMapper.findBoardLikes(socialId, boardNum);
    }

    // 유저 신고기능
    @Override
    public Long findReport(String socialId, String flag, String domNum) {
        Long findReport = null;
        Long resReport = null;

        if (flag.equals("board")){
            findReport = boardMapper.findReport(socialId, flag, domNum);
        }else{
            findReport = boardMapper.findReport(socialId, flag, domNum);
        }

        if (findReport != 0) {
            return 0L;
        } else {
            resReport = boardMapper.insertReport(socialId, flag, domNum);
        }
        return resReport;
    }


}
