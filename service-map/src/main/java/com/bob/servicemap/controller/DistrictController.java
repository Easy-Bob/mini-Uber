package com.bob.servicemap.controller;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.servicemap.service.DicDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/11.
 * Description:
 */
@RestController
public class DistrictController {

    @Autowired
    private DicDistrictService dicDistrictService;

    @GetMapping("/dic-district")
    public ResponseResult initDistrict(String keywords){

        return dicDistrictService.initDicDistrict(keywords);
    }
}
