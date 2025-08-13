package com.bob.servicemap.controller;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.servicemap.service.ServiceFromMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@RestController
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private ServiceFromMapService serviceFromMapService;

    @PostMapping("/add")
    public ResponseResult add(String name){
        return serviceFromMapService.add(name);
    }
}
