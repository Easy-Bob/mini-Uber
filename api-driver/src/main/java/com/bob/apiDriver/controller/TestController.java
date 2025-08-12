package com.bob.apiDriver.controller;

import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@RestController
public class TestController {

    @GetMapping("/auth")
    public String testAuth(){
        return "auth";
    }

    @GetMapping("/noauth")
    public ResponseResult testNoAuth(){
        return ResponseResult.success("");
    }
}
