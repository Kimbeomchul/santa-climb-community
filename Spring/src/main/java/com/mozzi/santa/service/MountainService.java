package com.mozzi.santa.service;

import com.mozzi.santa.dto.Climb;
import com.mozzi.santa.dto.Mountain;

import java.util.List;

public interface MountainService {

    /**
     * 산 전체검색 < paging >
     */
    List<Mountain> selectMountainList(long start , long end ,String socialId);

    /**
     * 산 상세검색
     */
    Mountain selectMountainDetail(String socialId, String mtNum);

    /**
     * 산 검색
     */
    List<Mountain> selectSearchedMountainList(String socialId,String searchWord);

    /**
     * 산 좋아요
     */
    Long updateMtLikes(String socialId, String mntcode);

    Long findMtLikes(String socialId, String mntcode);

    Long insertMtLikes(String socialId, String mntcode);

    Long deleteMtLikes(String socialId, String mntcode);

    /**
     * 즐겨찾는 산
     */
    List<Mountain> selectFavoriteMt(String socialId);

    /**
     * 배너 산
     */
    Mountain selectBannerMountain();

    /**
     * 산 요청
     */
    Long insertMountainRequest(String socialId, Mountain mountain);

    /**
     * 지도에 산 뿌리기
     */
    List<Mountain> selectMapMountainList(String socialId);

    /**
     * 등산 정보 저장
     */
    Long insertClimbResult(String socialId, String time, String name, String distance, String location, String fintime);

    /**
     * 등산 정보 조회
     */
    List<Climb> selectClimbResult(String socialId);

    /**
     * 등산 상세조회
     */
    Climb selectClimbDetailResult(String socialId, String climbNum);

    /**
     * 산검색 ( Lat,Lon )
     */
    List<Mountain> selectSearchedMountainListLatLon(String searchWord);
}
