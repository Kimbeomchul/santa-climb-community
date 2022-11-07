package com.mozzi.santa.service;

import com.mozzi.santa.dto.Board;
import com.mozzi.santa.dto.BoardComment;
import com.mozzi.santa.dto.BoardDetail;
import com.mozzi.santa.dto.Member;

import java.util.List;

public interface BoardService {

    /**
     *  게시판조회
     */
    List<Board> selectBoardList(long start, long end, String socialId ,String options);

    /**
     *  게시판조회
     */
    List<Board> selectBoardListPopular(long start, long end , String socialId ,String options);

    /**
     *  게시판 상세조회
     */
    BoardDetail selectBoardDetail(String socialId, String boardNum);

    /**
     * 게시판 좋아요 업데이트
     * */
    Long updateLikes(String socialId, String boardNum);

    /**
     * 게시판 좋아요 여부 판단
     * */
    Long findBoardLikes(String socialId, String boardNum);

    /**
     * 게시판 좋아요 생성
     * */
    Long insertLikes(String socialId, String boardNum);

    /**
     * 게시판 좋아요 삭제
     * */
    Long deleteLikes(String socialId, String boardNum);

    /**
     * 게시판 댓글조회
     * */
    List<BoardComment> selectBoardComments(String socialId,String boardNum);

    /**
     * 게시판 댓글쓰기
     * */
    Long insertComments(String boardNum , String socialId, String comments , String reply);

    /**
     * 게시판 삭제
     * */
    Long deleteBoard(String boardNum);

    /**
     * 추천게시글 세개
     */
    List<Board> selectRecommendBoardList(String socialId);

    /**
     * 게시판 검색
     */
    List<Board> selectSearchedBoardList(String socialId,  String searchWord);

    /**
     * 프로필 게시판 검색
     */
    List<Board> selectSearchProfileBoardList(String mySocialId,  String socialId);

    /**
     * 글작성
     */
    Long insertBoard(String socialId, Board board);

    /**
     * 글수정
     */
    Long updateBoard(Board board ,String boardNum);



    /**
     * 댓글 좋아요 여부 판단
     * */
    Long findCommentLikes(String socialId, String commentNum);

    /**
     * 댓글 좋아요 여부 판단
     * */
    Long updateCommentLikes(String socialId, String commentNum);

    /**
     * 댓글 좋아요 생성
     * */
    Long insertCommentLikes(String socialId, String commentNum);

    /**
     * 댓글 좋아요 삭제
     * */
    Long deleteCommentLikes(String socialId, String commentNum);


    /**
     * 댓글 삭제
     */
    Long deleteComment(String socialId, String commentsNum);

    /**
     * 댓글 전체삭제
     */
    Long deleteAllReply(String socialId, String commentsNum);

    /**
     * 답글 좋아요삭제
     */
    Long deleteReplyLikes(String socialId, String commentsNum);

    /**
     * 게시글 작성자찾기
     */
    String selectBoardWriter(String boardNum);

    /**
     * 유저 벤기능
     */
    Long insertBanUser(String socialId, String banSocialId);

    /**
     * 유저 신고기능
     */
    Long findReport(String socialId, String flag, String domNum);

}
