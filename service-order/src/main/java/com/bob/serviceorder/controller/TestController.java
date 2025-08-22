package com.bob.serviceorder.controller;

import com.bob.serviceorder.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/17.
 * Description:
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "test service-order";
    }



    public String dispatchRealTimeOrder(@PathVariable("orderId") long orderId){
        return "test";
    }
}
