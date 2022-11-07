package com.mozzi.santa.controller;

import com.mozzi.santa.config.S3Uploader;
import com.mozzi.santa.dto.Climb;
import com.mozzi.santa.dto.Mountain;
import com.mozzi.santa.service.MountainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.mozzi.santa.config.Constants.SESSION_NAME;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mt")
public class MountainController {

    private final MountainService mountainService;
    private final S3Uploader s3Uploader;
    @GetMapping("/list")
    public String mountainList(@SessionAttribute(name= SESSION_NAME, required = false) String socialId,Model model){
        List<Mountain> mt = mountainService.selectMountainList(1,50 ,socialId);
        log.debug("mountainList = {}" , mt);

        model.addAttribute("mt", mt);
        return "mountain/mountain_search";
    }

    @GetMapping("/mapList")
    @ResponseBody
    public List<Mountain> mountainMapList(@SessionAttribute(name= SESSION_NAME, required = false) String socialId){
        List<Mountain> mt = mountainService.selectMapMountainList(socialId);
        log.debug("mountainList = {}" , mt);
        return mt;
    }

    @GetMapping("/detail/{mtNum}")
    public String mountainDetail(@SessionAttribute(name= SESSION_NAME, required = false) String socialId,@PathVariable String mtNum,Model model){
        Mountain mt = mountainService.selectMountainDetail(socialId,mtNum);
        log.debug("mountainDetail = {}" , mt);

        model.addAttribute("mt", mt);
        return "mountain/mountain_detail";
    }

    @GetMapping("/search/{searchWord}")
    public String searchMt(@SessionAttribute(name= SESSION_NAME, required = false) String socialId,@PathVariable String searchWord,Model model){
        List<Mountain> mt = mountainService.selectSearchedMountainList(socialId,searchWord);
        log.debug("searchMt = {}" , mt);

        model.addAttribute("search", searchWord);
        if(mt.size() == 0){
            return "mountain/mountain_search_empty";
        }
        model.addAttribute("mt", mt);
        return "mountain/mountain_search";
    }

    @GetMapping("/favorite")
    public String favoriteMt(@SessionAttribute(name= SESSION_NAME, required = false) String socialId,Model model){
        List<Mountain> mt = mountainService.selectFavoriteMt(socialId);
        log.debug("favoriteMt = {}" , mt);

        if(mt.size() == 0){
            return "mountain/mountain_search_empty";
        }

        model.addAttribute("mt", mt);
        return "mountain/mountain_search";
    }

    @RequestMapping("/list/{start}")
    public @ResponseBody List<Mountain> pagingMt(@SessionAttribute(name= SESSION_NAME, required = false) String socialId,@PathVariable long start, Model model){
        List<Mountain> mt = mountainService.selectMountainList(start,start + 50 , socialId);
        log.debug("pagingMt = {}" , mt);

        return mt;
    }

    @GetMapping("/empty")
    public String mountainListEmpty(){
        return "mountain/mountain_search_empty";
    }

    @GetMapping("/request")
    public String mountainRequest(){
        return "mountain/mountain_request";
    }

    @PostMapping("/request")
    public String modifyMountainRequest(@SessionAttribute(name= SESSION_NAME, required = false) String socialId,Mountain mountain){
        Long resMountainReq = mountainService.insertMountainRequest(socialId , mountain);
        return "redirect:https://santa-community.co.kr/mt/list";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        String fileUuid = UUID.randomUUID().toString();
        return s3Uploader.uploadConvert(multipartFile, "request", fileUuid);
    }

    // 좋아요
    @PostMapping("/like/{mntcode}")
    @ResponseBody
    public Long mountainLikes(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @PathVariable String mntcode){
        Long resLikes = mountainService.updateMtLikes(socialId,mntcode);
        // 2L 삽입 3L 삭제
        if(resLikes == 0){
            log.info("############### 좋아요 에러 /like/{mntcode}");
        }

        return resLikes;
    }

    @PostMapping("/climb/result")
    @ResponseBody
    public Long mountainClimbResult(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @RequestParam String time, @RequestParam String name, @RequestParam String distance, @RequestParam String location , @RequestParam String fintime) {
        Long resClimb = mountainService.insertClimbResult(socialId, time , name , distance, location , fintime);
        if(resClimb == 0){
            log.info("################# 사용자 등산정보 저장 실패  = {} {} {} {} {}", socialId,time,name,distance ,location);
        }

        return resClimb;
    }

    @GetMapping("/climb/reward")
    public String climbResultReward(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, Model model){
        List<Climb> rewards = mountainService.selectClimbResult(socialId);
        log.info("rewards = {}" ,rewards);
        model.addAttribute("result", rewards);
        return "reward";
    }

    @GetMapping("/climb/reward/detail/{climbNum}")
    public String climbResultRewardDetail(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, Model model , @PathVariable String climbNum){
        Climb rewards = mountainService.selectClimbDetailResult(socialId, climbNum);
        log.info(rewards.getClimbLocation());
        model.addAttribute("result", rewards);
        model.addAttribute("climbNum", climbNum);
        return "rewardDetail";
    }

    @GetMapping("/map/search/{searchWord}")
    @ResponseBody
    public List<Mountain> searchMountain(@PathVariable String searchWord){
        List<Mountain> searchList = mountainService.selectSearchedMountainListLatLon(searchWord);
        return searchList;
    }
}
