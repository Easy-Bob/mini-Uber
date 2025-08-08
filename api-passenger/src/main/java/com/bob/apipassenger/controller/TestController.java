package com.bob.apipassenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "Test api passenger";
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/test-redis")
    public String testRedis() {
        redisTemplate.opsForValue().set("testKey", "Hello Redis!");
        return redisTemplate.opsForValue().get("testKey");
    }
}
