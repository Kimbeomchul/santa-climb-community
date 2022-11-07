package com.mozzi.santa.controller;

import com.mozzi.santa.config.SessionListener;
import com.mozzi.santa.dto.BugReport;
import com.mozzi.santa.dto.Location;
import com.mozzi.santa.dto.Member;
import com.mozzi.santa.dto.Mountain;
import com.mozzi.santa.service.MemberService;
import com.mozzi.santa.service.MountainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

import static com.mozzi.santa.config.Constants.CLIMB_STATUS;
import static com.mozzi.santa.config.Constants.SESSION_NAME;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    @Value("${weather.id}")
    private String weather_id;
    private final MemberService memberService;
    private final MountainService mountainService;
    private final SessionListener sessionListener;

    @GetMapping("/")
    public String indexPage(@SessionAttribute(name= SESSION_NAME, required = false) String socialId){
        if(socialId != null){
            return "redirect:/main";
        }
        return "index";
    }

    @GetMapping("/main")
    public String maMain(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @SessionAttribute(name= CLIMB_STATUS, required = false) String climb,Model model){
        // 현재사용자 조회
        Member member = memberService.selectMemberInfo(socialId);
        log.debug("Main Page Member Info = {}", member);

        // 세션정보조회 현재로그인중사용자 찾기
        List loginUsers = sessionListener.getLoginList();
        Long cntLoginUsers = sessionListener.getTotalUser();

        // 로그인중사용자 조회
        List<Member> loginMembers = memberService.selectAllLoginUsers(loginUsers, socialId);
        log.debug("Main Page Login Users Info = {}" , loginMembers);

        // 배너 산 조회
        Mountain bannerMountain = mountainService.selectBannerMountain();


        if(loginMembers.size() > 20){
            List<Member> resizedLoginMember = loginMembers.subList(0,20);
            model.addAttribute("loginMembers", resizedLoginMember);
        }else{
            model.addAttribute("loginMembers", loginMembers);
        }
        model.addAttribute("allLoginMembers", loginMembers);
        model.addAttribute("banner", bannerMountain);
        model.addAttribute("climb",climb);
        model.addAttribute("member", member);
        model.addAttribute("cntLoginUsers", cntLoginUsers);

        return "maMain";
    }

    @GetMapping("/map")
    public String map(Model model,@SessionAttribute(name= SESSION_NAME, required = false) String socialId){
        if(!socialId.replaceAll("-","").equals(socialId)){
            model.addAttribute("guest","Y");
        }else{
            model.addAttribute("guest","N");
        }
        return "map";
    }

    @GetMapping("/map/climb")
    public String mapClimb(Model model){
        return "map_climb";
    }


    @GetMapping("/profile")
    public String profile(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, Model model){

        if(!socialId.replaceAll("-","").equals(socialId)){
            return "redirect:https://santa-community.co.kr/main?guest=1";
        }

        // 세션정보조회 현재로그인중사용자 찾기
        List loginUsers = sessionListener.getLoginList();

        // 팔로워 조회
        List<Member> followList = memberService.selectFollowMembers(socialId);
        // 팔로잉 조회
        List<Member> followingList = memberService.selectFollowingMembers(socialId);

        // 유저를 찾기
        Member member = memberService.selectMemberInfo(socialId);
        log.debug("Profile Page Member Info = {}", member);

        model.addAttribute("loginUsers", loginUsers);
        model.addAttribute("follower", followList);
        model.addAttribute("following", followingList);
        model.addAttribute("member", member);
        return "profile";
    }

    @GetMapping("/notice")
    public String notice(){
        return "setting/notice";
    }

    @GetMapping("/setting")
    public String setting(){
        return "setting/setting";
    }

    @GetMapping("/info")
    public String myInfo(){
        return "setting/my_info";
    }

    @GetMapping("/profile/other")
    public String otherProfile(){
        return "other_profile";
    }

    @GetMapping("/profile/edit/{socialId}")
    public String profileEdit(@SessionAttribute(name= SESSION_NAME, required = false) String mySocialId, @PathVariable String socialId, Model model){
        if(!mySocialId.equals(socialId)){
            log.info("profile edit == 비정상적인 접근 : 사용자 미일치 : 비정상접근 시도자 = {} , 접근하려던 유저정보 = {}" , socialId , mySocialId);
            return "redirect:/profile/edit/"+mySocialId;
        }
        Member member = memberService.selectMemberInfo(socialId);

        model.addAttribute("member", member);
        return "setting/profile_edit";
    }

    @PostMapping("/profile/edit")
    public String profileUpdate(Member member, @SessionAttribute(name= SESSION_NAME, required = false) String socialId){
        if(member.getNickname().length() > 5){
            return "redirect:/profile/edit/ + socialId";
        }
        memberService.updateMemberInfo(member);

        return "redirect:/profile";
    }

    // 팔로우기능
    @PostMapping("/follow/{socialId}")
    @ResponseBody
    public Long followMember(@SessionAttribute(name= SESSION_NAME, required = false) String mySocialId, @PathVariable String socialId){
        if(socialId.equals(mySocialId)){
            log.info("자기 자신을 팔로우 시도함 = {}", socialId);
            return -1L;
        }
        // 성공 1 실패 0
        Long resFollow = memberService.followMember(mySocialId , socialId);
        return resFollow;
    }


    // 회원탈퇴
    @PostMapping("/quit")
    public String quitSanta(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, HttpServletRequest request){

        log.info("################## 회원탈퇴 요청됨 = {}", socialId);
        memberService.quitMember(socialId);
        HttpSession session = request.getSession(false);
        // 세션존재시 세션제거
        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }


    // 날씨 요청
    @GetMapping("/weather/{lat}/{lon}")
    @ResponseBody
    public String getWeather(@PathVariable String lat, @PathVariable String lon){


        RestTemplate rt = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();

        HttpEntity<MultiValueMap<String,String>> weatherRequest = new HttpEntity<>(params,httpHeaders);

        ResponseEntity<String> response = rt.exchange(
                "https://api.openweathermap.org/data/2.5/weather?lat="+ lat +"&lon="+ lon +"&appid=" + weather_id,
                HttpMethod.GET,
                weatherRequest,
                String.class
        );
        JSONObject jo = new JSONObject(response.getBody());

        return String.valueOf(jo);
    }


    // bug report
    @GetMapping("/bug/report")
    public String bugReport(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, Model model){
        List<BugReport> resBugReport = memberService.selectBugReport(socialId);
        model.addAttribute("result", resBugReport);
        return "setting/report";
    }

    @PostMapping("/bug/report/request")
    @ResponseBody
    public Long insertBugReport(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, @RequestParam String page, @RequestParam String detail){
        Long resBugReport = memberService.insertBugReport(socialId, page, detail);
        return resBugReport;
    }

    @GetMapping("/bug/report/request")
    @ResponseBody
    public List<BugReport> insertBugReport(@SessionAttribute(name= SESSION_NAME, required = false) String socialId){
        List<BugReport> resBugReport = memberService.selectBugReport(socialId);
        return resBugReport;
    }

    // Testing Download Application
    @GetMapping("/download")
    public String downloadApplication(){
        return "download";
    }

}
