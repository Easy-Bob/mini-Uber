package com.bob.servicemap.remote;

import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.ServiceResponsse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 调用高德猎鹰，执行轨迹管理
 */

@Service
@Slf4j
public class ServiceClient {

    @Value("${amap.key}")
    private String key;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult add(String name){

        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.SERVICE_ADD_URL)
                .append("?")
                .append("key=" + key)
                .append("&")
                .append("name=" + name);

//        log.info(url.toString());
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null, String.class);

        String body = forEntity.getBody();
        JSONObject result = JSONObject.fromObject(body);
        JSONObject data = result.getJSONObject("data");
        String sid = data.getString("sid");

        ServiceResponsse serviceResponsse = new ServiceResponsse();
        serviceResponsse.setSid(sid);
        return ResponseResult.success(serviceResponsse);
    }
}
