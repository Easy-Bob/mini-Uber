package com.bob.servicemap.remote;

import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.ServiceResponsse;
import com.bob.internalcommon.constant.response.TerminalResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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

    public ResponseResult<TerminalResponse> add(String name, String desc){
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TERMINAL_ADD_URL)
                .append("?")
                .append("key=" + key)
                .append("&")
                .append("sid=" + sid)
                .append("&")
                .append("name=" + name)
                .append("&")
                .append("desc=" + desc);
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null, String.class);

        String body = forEntity.getBody();
        JSONObject result = JSONObject.fromObject(body);
        JSONObject data = result.getJSONObject("data");
        String tid = data.getString("tid");

        TerminalResponse terminalResponse = new TerminalResponse();
        terminalResponse.setTid(tid);
        return ResponseResult.success("");
    }

    public ResponseResult aroundSearch(String center, Integer radius){
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TERMINAL_AROUND_URL)
                .append("?")
                .append("key=" + key)
                .append("&")
                .append("sid=" + sid)
                .append("&")
                .append("center=" + center)
                .append("&")
                .append("radius=" + radius);
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null, String.class);

        String body = forEntity.getBody();
        JSONObject result = JSONObject.fromObject(body);
        JSONObject data = result.getJSONObject("data");

        List<TerminalResponse> terminalResponseList = new ArrayList<>();

        JSONArray results = data.getJSONArray("results");
        for(int i = 0; i < results.size(); i++){
            TerminalResponse terminalResponse = new TerminalResponse();
            JSONObject jsonObject = results.getJSONObject(i);

            if(jsonObject.has("desc")){
                Long carId = jsonObject.getLong("desc");
                terminalResponse.setCarId(carId);
            }
            String tid = jsonObject.getString("tid");

            terminalResponse.setTid(tid);
            terminalResponseList.add(terminalResponse);
        }

        return ResponseResult.success(terminalResponseList);
    }
}
