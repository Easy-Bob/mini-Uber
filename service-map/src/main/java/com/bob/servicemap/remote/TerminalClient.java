package com.bob.servicemap.remote;

import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.ServiceResponsse;
import com.bob.internalcommon.constant.response.TerminalResponse;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@Service
public class TerminalClient {

    @Value("${amap.key}")
    private String key;

    @Value("${amap.sid}")
    private String sid;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult add(String name){

        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TERMINAL_ADD_URL)
                .append("?")
                .append("key=" + key)
                .append("&")
                .append("sid=" + sid)
                .append("&")
                .append("name=" + name);

        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null, String.class);

        String body = forEntity.getBody();
        JSONObject result = JSONObject.fromObject(body);
        JSONObject data = result.getJSONObject("data");
        String tid = data.getString("tid");

        TerminalResponse terminalResponse = new TerminalResponse();
        terminalResponse.setTid(tid);
        return ResponseResult.success(terminalResponse);
    }
}
