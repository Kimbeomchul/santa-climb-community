package com.mozzi.santa.controller;

import com.mozzi.santa.dto.Member;
import com.mozzi.santa.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

import static com.mozzi.santa.config.Constants.SERVER_URL;
import static com.mozzi.santa.config.Constants.SESSION_NAME;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    
    @Value("${kakao.rest}")
    private String kakao_rest;
    @Value("${naver.client}")
    private String naver_client;
    @Value("${naver.secret}")
    private String naver_secret;
    @Value("${google.client}")
    private String google_client;
    @Value("${google.secret}")
    private String google_secret;
    @Value("${facebook.client}")
    private String facebook_client;
    @Value("${facebook.secret}")
    private String facebook_secret;


    private final MemberService memberService;


    // 네이버 로그인 state 난수 생성
    public String generateState()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    // 로그인 분기
    @GetMapping("/login/{with}")
    public RedirectView MainLogin(@PathVariable("with") String login_with){
        RedirectView rv = new RedirectView();
        switch (login_with){
            case "guest":
                rv.setUrl("https://santa-community.co.kr/auth/test/callback");
                break;
            case "kakao":
                rv.setUrl("https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+ kakao_rest +"&redirect_uri="+SERVER_URL+"/auth/kakao/callback");
                break;
            case "google":
                rv.setUrl("https://accounts.google.com/o/oauth2/v2/auth?client_id="+ google_client +"&redirect_uri="+SERVER_URL+"/auth/google/callback&response_type=code&scope=email%20profile%20openid&access_type=offline");
                break;
            case "naver":
                String state = generateState();
                rv.setUrl("https://nid.naver.com/oauth2.0/authorize?client_id="+ naver_client +"&response_type=code&redirect_uri="+SERVER_URL+"/auth/naver/callback&state="+ state);
                break;
            case "facebook":
                rv.setUrl("https://www.facebook.com/v12.0/dialog/oauth?client_id="+ facebook_client +"&redirect_uri="+SERVER_URL+"/auth/facebook/callback&response_type=code");
                break;
            case "imsi":
                rv.setUrl("https://santa-community.co.kr/auth/imsi/callback");
                break;
            default:
                log.debug("############## MainLogin.login error");
        }
        return rv;
    }


    /**
     * 인증번호 로그인
     */
    @RequestMapping(value = "auth/imsi/callback")
    public String authLogin(HttpServletRequest request, Model model){

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_NAME, "googlePolicyCheck");
        return "redirect:/main";
    }

    // TODO : Exception advice 생성
    // TODO : GUEST 인증번호 삭제 

    /**
     * GUEST 로그인
     */
    @RequestMapping(value = "auth/test/callback")
    public String guestLogin(HttpServletRequest request, Model model){
        String guestUUID = UUID.randomUUID().toString();
        Long guestCnt = memberService.selectGuestCnt();

        Member guestMember = new Member();
        guestMember.setNickname("익명"+guestCnt);
        guestMember.setSocialId(guestUUID);
        // Guest 회원저장
        Long signup = memberService.signupMember(guestMember);
        log.debug("GUEST SIGNUP = {}" , signup);

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_NAME, guestUUID);
        return "redirect:/main";
    }


    /**
     * 작성자 : beomchul.kim@lotte.net
     * 카카오 로그인
     */
    @RequestMapping(value = "auth/kakao/callback")
    public String KakaoLogin(@RequestParam("code") String code ,HttpServletRequest request, Model model){


        RestTemplate rt = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id", kakao_rest);
        params.add("redirect_uri",SERVER_URL + "/auth/kakao/callback");
        params.add("code",code);

        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = new HttpEntity<>(params,httpHeaders);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // 토큰값 Json 형식으로 가져오기위해 생성
        JSONObject jo = new JSONObject(response.getBody());

        // 토큰결과값
        log.debug("kakao token result = {} " , response);

        RestTemplate rt2 = new RestTemplate();
        HttpHeaders headers2 = new HttpHeaders();

        headers2.add("Authorization", "Bearer "+ jo.get("access_token"));
        headers2.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String,String >> kakaoProfileRequest2= new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );

        // 토큰을 사용하여 사용자 정보 추출
        JSONObject jo2 = new JSONObject(response2.getBody());
        log.debug("###### kakao login = {}", jo2);

        // 멤버아이디로 디비조회 후 존재시 skip
        Long mmbrNum = memberService.selectMemberId(String.valueOf(String.valueOf(jo2.get("id"))));
        if(mmbrNum != null){
            // DB 조회후 회원을 찾았을경우 세션생성 및 값저장
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_NAME, String.valueOf(jo2.get("id")));
            return "redirect:/main";
        }
        try{
            model.addAttribute("email",jo2.getJSONObject("kakao_account").get("email"));
        }catch(Exception e){
            log.debug("KAKAO 이메일 권한 미획득");
        }

        // TODO : 예외처리
        try{
            model.addAttribute("socialType","kakao");
            model.addAttribute("socialId",String.valueOf(jo2.get("id")));
            model.addAttribute("memberImage",String.valueOf(jo2.getJSONObject("properties").get("profile_image")));
            return "sign/signup";
        }catch(Exception e){
            log.debug("Kakao Signup Error = {}", e);
            return "redirect:/";
        }

    }


    /**
     * 작성자 : beomchul.kim@lotte.net
     * 페이스북 로그인
     */
    @RequestMapping(value = "auth/facebook/callback")
    public String FacebookLogin(@RequestParam("code") String code, HttpServletRequest request, Model model) {

        RestTemplate rt = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("client_id", facebook_client);
        params.add("client_secret", facebook_secret);
        params.add("code", code);
        params.add("redirect_uri", SERVER_URL + "/auth/facebook/callback");

        HttpEntity<MultiValueMap<String,String>> FacebookTokenRequest = new HttpEntity<>(params,httpHeaders);

        ResponseEntity<String> response = rt.exchange(
                "https://graph.facebook.com/v12.0/oauth/access_token",
                HttpMethod.POST,
                FacebookTokenRequest,
                String.class
        );

        // 토큰값 Json 형식으로 가져오기위해 생성
        JSONObject jo = new JSONObject(response.getBody());


        // 페이스북 Access Token
        log.debug("facebook token result = {} " , jo);

        // 사용자 정보 불러오기
        RestTemplate rt2 = new RestTemplate();

        String requestUrl = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/me").queryParam("access_token", jo.get("access_token")).toUriString();
        HttpEntity<MultiValueMap<String,String>> FacebookPersonalData = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response2 = rt2.exchange(
                requestUrl,
                HttpMethod.GET,
                FacebookPersonalData,
                String.class
        );

        JSONObject jo2 = new JSONObject(response2.getBody());

        // 유저정보 < 이름 , 아이디 값 >
        log.debug("##### facebook login = {}" , jo2);

        // 멤버아이디로 디비조회 후 존재시 skip
        Long mmbrNum = memberService.selectMemberId(String.valueOf(jo2.get("id")));
        if(mmbrNum != null){
            // DB 조회후 회원을 찾았을경우 세션생성 및 값저장
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_NAME, String.valueOf(jo2.get("id")));
            return "redirect:/main";
        }

        // TODO : 예외처리
        try{
            model.addAttribute("socialType","facebook");
            model.addAttribute("socialId",String.valueOf(jo2.get("id")));
            return "sign/signup";
        }catch(Exception e){
            log.debug("Facebook Signup Error = {}", e);
            return "redirect:/";
        }
    }


    /**
     * 작성자 : beomchul.kim@lotte.net
     * 구글 로그인
     */
    @RequestMapping(value = "auth/google/callback")
    public String GoogleLogin(@RequestParam("code") String code, HttpServletRequest request , Model model) {
        ModelAndView mav = new ModelAndView();
        RestTemplate rt = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("client_id", google_client);
        params.add("client_secret", google_secret);
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", SERVER_URL + "/auth/google/callback");

        HttpEntity<MultiValueMap<String,String>> GoogleTokenRequest = new HttpEntity<>(params,httpHeaders);

        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                GoogleTokenRequest,
                String.class
        );

        // 토큰값 Json 형식으로 가져오기위해 생성
        JSONObject jo = new JSONObject(response.getBody());

        log.debug("google Access token result = {} " , jo.get("access_token"));
        log.debug("google Id token result = {} " ,  jo.get("id_token"));


        // 구글 사용자정보 Url
        String requestUrl = UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo").queryParam("id_token", jo.get("id_token")).toUriString();

        // 사용자 정보 불러오기
        RestTemplate rt2 = new RestTemplate();
        HttpEntity<MultiValueMap<String,String>> GooglePersonalData = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response2 = rt2.exchange(
                requestUrl,
                HttpMethod.GET,
                GooglePersonalData,
                String.class
        );

        JSONObject jo2 = new JSONObject(response2.getBody());


        //사용자 정보 추출
        log.debug("######## google login = {}", jo2);

        // 멤버아이디로 디비조회 후 존재시 skip
        Long mmbrNum = memberService.selectMemberId(String.valueOf(String.valueOf(jo2.get("sub"))));
        if(mmbrNum != null){
            // DB 조회후 회원을 찾았을경우 세션생성 및 값저장
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_NAME, String.valueOf(String.valueOf(jo2.get("sub"))));
            return "redirect:/main";
        }

        // TODO : 예외처리
        try{
            model.addAttribute("socialType","google");
            model.addAttribute("socialId",String.valueOf(jo2.get("sub")));
            model.addAttribute("memberImage",String.valueOf(jo2.get("picture")));
            model.addAttribute("email",jo2.get("email"));
            return "sign/signup";
        }catch(Exception e){
            log.debug("Google Signup Error = {}", e);
            return "redirect:/";
        }
    }


    /**
     * 작성자 : beomchul.kim@lotte.net
     * 네이버 로그인
     */
    @RequestMapping(value = "auth/naver/callback")
    public String NaverLogin(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletRequest request, Model model){

        RestTemplate rt = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("client_id", naver_client);
        params.add("client_secret", naver_secret);
        params.add("grant_type", "authorization_code");
        params.add("state", state);  // state 일치를 확인
        params.add("code", code);

        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = new HttpEntity<>(params,httpHeaders);

        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // 토큰값 Json 형식으로 가져오기위해 생성
        JSONObject jo = new JSONObject(response.getBody());

        // 토큰결과값
        log.debug("naver Id token result = {} " ,  response);


        RestTemplate rt2 = new RestTemplate();
        HttpHeaders headers2 = new HttpHeaders();

        headers2.add("Authorization", "Bearer "+ jo.get("access_token"));
        headers2.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String,String >> kakaoProfileRequest2= new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = rt2.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );

        // 토큰을 사용하여 사용자 정보 추출
        JSONObject jo2 = new JSONObject(response2.getBody());

        log.debug("##### naver login = {}" , jo2);

        // 멤버아이디로 디비조회 후 존재시 skip
        Long mmbrNum = memberService.selectMemberId(String.valueOf(jo2.getJSONObject("response").get("id")).replaceAll("-", ""));
        if(mmbrNum != null){
            // DB 조회후 회원을 찾았을경우 세션생성 및 값저장
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_NAME, String.valueOf(jo2.getJSONObject("response").get("id")).replaceAll("-", ""));
            return "redirect:/main";
        }

        // TODO : 예외처리
        try{
            model.addAttribute("socialType","naver");
            model.addAttribute("socialId",String.valueOf(jo2.getJSONObject("response").get("id")).replaceAll("-", ""));
            model.addAttribute("memberImage",String.valueOf(jo2.getJSONObject("response").get("profile_image")));
            model.addAttribute("email",jo2.getJSONObject("response").get("email"));
            return "sign/signup";
        }catch(Exception e){
            log.debug("Naver Signup Error = {}", e);
            return "redirect:/";
        }
    }




    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        // 세션존재시 세션제거
        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/guest/logout")
    public String guestLogout(@SessionAttribute(name= SESSION_NAME, required = false) String socialId, HttpServletRequest request) {

        if(socialId.replaceAll("-","").equals(socialId)){
            return "redirect:/";
        }

        HttpSession session = request.getSession(false);

        // 세션존재시 세션제거
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

}
