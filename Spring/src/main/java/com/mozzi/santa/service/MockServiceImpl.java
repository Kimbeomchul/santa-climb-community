package com.mozzi.santa.service;

import com.mozzi.santa.dto.tempDTO;
import com.mozzi.santa.mapper.MountainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockServiceImpl implements MockService {

    private final MountainMapper santaMapper;

    @Override
    public void saveMt(String image , String mntilistno) {
        santaMapper.saveMt(image , mntilistno);
    }
}
