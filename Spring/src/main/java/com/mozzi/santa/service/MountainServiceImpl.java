package com.mozzi.santa.service;

import com.mozzi.santa.dto.Climb;
import com.mozzi.santa.dto.Mountain;
import com.mozzi.santa.mapper.MountainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MountainServiceImpl implements MountainService {

    private final MountainMapper mountainMapper;


    /**
     * 산 전체검색 < paging >
     */
    @Override
    public List<Mountain> selectMountainList(long start , long end  ,String socialId) {
        return mountainMapper.selectMountainList(start ,end , socialId);
    }

    /**
     * 산 상세검색
     */
    @Override
    public Mountain selectMountainDetail(String socialId, String mtNum) {
        return mountainMapper.selectMountainDetail(socialId, mtNum);
    }

    /**
     * 산 검색
     */
    @Override
    public List<Mountain> selectSearchedMountainList(String socialId,String searchWord) {
        return mountainMapper.selectSearchedMountainList(socialId, searchWord);
    }


    /**
     * 산 좋아요
     */
    @Override
    public Long updateMtLikes(String socialId, String mntcode) {

        Long findLikeYsno = this.findMtLikes(socialId, mntcode);
        Long successYsno = 0L;

        // 좋아요상태가 아닐시
        if(findLikeYsno == 0){
            this.insertMtLikes(socialId, mntcode);
            successYsno = 2L;
        }else{ // 좋아요 상태일시
            this.deleteMtLikes(socialId, mntcode);
            successYsno = 3L;
        }
        return successYsno;
    }

    @Override
    public Long insertMtLikes(String socialId, String mntcode) {
        return mountainMapper.insertMtLikes(socialId, mntcode);
    }

    @Override
    public Long deleteMtLikes(String socialId, String mntcode) {
        return mountainMapper.deleteMtLikes(socialId, mntcode);
    }

    @Override
    public List<Mountain> selectFavoriteMt(String socialId) {
        return mountainMapper.selectFavoriteMt(socialId);
    }


    // 배너 산
    @Override
    public Mountain selectBannerMountain() {
        return mountainMapper.selectBannerMountain();
    }

    @Override
    public Long insertMountainRequest(String socialId, Mountain mountain) {
        return mountainMapper.insertMountainRequest(socialId, mountain);
    }

    // 게시판의 좋아요 여부 판단
    @Override
    public Long findMtLikes(String socialId, String mntcode) {
        return mountainMapper.findMtLikes(socialId, mntcode);
    }


    /**
     * 지도에 위치정보 있는 산 뿌리기
     */
    @Override
    public List<Mountain> selectMapMountainList(String socialId) {
        return mountainMapper.selectMapMountainList(socialId);
    }

    @Override
    public Long insertClimbResult(String socialId, String time, String name, String distance, String location, String fintime) {
        return mountainMapper.insertClimbResult(socialId, time, name, distance, location , fintime);
    }

    @Override
    public List<Climb> selectClimbResult(String socialId) {
        return mountainMapper.selectClimbResult(socialId);
    }

    @Override
    public Climb selectClimbDetailResult(String socialId, String climbNum) {
        return mountainMapper.selectClimbDetailResult(socialId, climbNum);
    }

    @Override
    public List<Mountain> selectSearchedMountainListLatLon(String searchWord) {
        return mountainMapper.selectSearchedMountainListLatLon(searchWord);
    }
}
