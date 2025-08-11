package com.bob.servicemap.service;

import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 2025/8/11.
 * Description:
 */
@Service
@Slf4j
public class DicDistrictService {

    @Value("${amap.key}")
    private String amapKey;

    public ResponseResult initDicDistrict(String keywords){
//        https://restapi.amap.com/v3/config/district?keywords=%E4%B8%AD%E5%9B%BD&subdistrict=3&key=9be00c92bf761f26e41441740609a5ef

        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.DISTRICT_URL)
                .append("?")
                .append("keywords=")
                .append(keywords)
                .append("&")
                .append("subdistrict=3")
                .append("&")
                .append("key=")
                .append(amapKey);
//        log.info(url.toString());
        return ResponseResult.success();
    }
}
