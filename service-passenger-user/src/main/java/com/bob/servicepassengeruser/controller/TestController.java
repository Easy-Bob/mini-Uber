package com.bob.servicepassengeruser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/9.
 * Description:
 */
@RestController
public class TestController {

    @GetMapping
    public String test(){
        return "service-passenger-user";
    }
}
