package com.bob.servicemap.remote;

import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Sun on 2025/8/11.
 * Description:
 */

@Service
public class MapDistrictClient {
    @Value("${amap.key}")
    private String amapKey;

    @Autowired
    private RestTemplate restTemplate;

    public String dicDistrict(String keywords) {
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

        // 解析
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url.toString(), String.class);

        return forEntity.getBody();
    }
}
