package com.mozzi.santa.controller;

import com.mozzi.santa.dto.Member;
import com.mozzi.santa.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.mozzi.santa.config.Constants.SESSION_NAME;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/sign")
public class SignController {

    private final MemberService memberService;
    @GetMapping("/first")
    public String signup(){
        return "sign/signup";
    }

    @GetMapping("/second")
    public String signup2(){
        return "sign/signup2";
    }

    @PostMapping("/complete")
    public String signupComplete(Member member,HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute(SESSION_NAME, member.getSocialId());

        Long signup = memberService.signupMember(member);
        log.debug("signup = {}" , signup);

        return "sign/signcomp";
    }

    @GetMapping("/validate/{nickname}")
    @ResponseBody
    public Long validateNickname(@PathVariable String nickname){
        Long resValidate = memberService.selectUsername(nickname);
        log.debug("USER VAL = {}" , resValidate);
        return resValidate;
    }
    
}
