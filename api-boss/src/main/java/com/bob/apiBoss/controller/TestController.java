package com.bob.apiBoss.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "test-api-boss";
    }
}
