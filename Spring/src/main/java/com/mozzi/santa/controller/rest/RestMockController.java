package com.mozzi.santa.controller.rest;

import com.mozzi.santa.config.SessionListener;
import com.mozzi.santa.dto.tempDTO;
import com.mozzi.santa.service.MockService;
import com.mozzi.santa.service.MountainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.lang.System.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mock")
public class RestMockController {

    private final MockService mockService;
    private final SessionListener sessionListener;
    private final MountainService mountainService;
    @Value("${mountain.decode}")
    public String mountain_decode;

    // 현재로그인유저
    @GetMapping("/test")
    public void sessionAll(HttpSession session, HttpServletRequest request) {

        log.debug("LOGIN USERS INFO = {}" ,sessionListener.getLoginList());
        log.debug("LOGIN USERS COUNT = {}" ,sessionListener.getTotalUser());
    }


    // 산데이터 추가
    @GetMapping("/all/{pageNo}")
    @ResponseBody
    public String getAllMountains(@PathVariable String pageNo) {



            RestTemplate rt = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            HttpEntity<MultiValueMap<String, String>> MountainReq = new HttpEntity<>(params, httpHeaders);
            ResponseEntity<String> response = rt.exchange(
                    "http://apis.data.go.kr/1400000/service/cultureInfoService/mntInfoOpenAPI?searchWrd=&ServiceKey=" + mountain_decode + "&numOfRows=1&pageNo=" + pageNo,
                    HttpMethod.GET,
                    MountainReq,
                    String.class
            );
            // 토큰값 Json 형식으로 가져오기위해 생성
            JSONObject jo = new JSONObject(response.getBody());
            tempDTO td = new tempDTO();
            String tempList = String.valueOf(jo.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONObject("item").getInt("mntilistno"));



        return tempList;
    }

    @GetMapping("/save/{image}/{mntilistno}")
    @ResponseBody
    public String insertImage(@PathVariable String image, @PathVariable String mntilistno){

        log.info("image = {} , mntl = {}", image,mntilistno);
        mockService.saveMt("https://www.forest.go.kr/images/data/down/mountain/" + image , mntilistno);
        log.info("FIN == {} , {}", image,mntilistno);

        return "ok";
    }

    @GetMapping("/search/{name}/{pageNo}")
    public String mtSearchName(@PathVariable String name,@PathVariable String pageNo) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String,String>> MountainReq = new HttpEntity<>(params,httpHeaders);
        ResponseEntity<String> response = rt.exchange(
                "http://apis.data.go.kr/1400000/service/cultureInfoService/mntInfoOpenAPI?searchWrd="+name+"&ServiceKey="+ mountain_decode +"&numOfRows=20&pageNo=" + pageNo,
                HttpMethod.GET,
                MountainReq,
                String.class
        );
        // 토큰값 Json 형식으로 가져오기위해 생성
        JSONObject jo = new JSONObject(response.getBody());
        out.println(jo);
        String datas = String.valueOf(jo.getJSONObject("response").getJSONObject("body").getJSONObject("items"));
        log.debug("DATAS = {} " , datas);
        return datas;
    }


}