package com.bob.servicemap.controller;

import com.bob.internalcommon.constant.dto.DicDistrict;
import com.bob.servicemap.mapper.DicDistrictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "service map";
    }

    @Autowired
    private DicDistrictMapper mapper;

    @GetMapping("/testMap")
    public String testMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("address_code", "110000");
        List<DicDistrict> dicDistricts = mapper.selectByMap(map);
        System.out.println(dicDistricts);
        return "test-map";
    }
}
