package com.bob.servicemap.remote;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.TerminalResponse;
import com.bob.internalcommon.constant.response.TrackResponse;
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
public class TrackClient {

    @Value("${amap.key}")
    private String key;

    @Value("${amap.sid}")
    private String sid;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult<TrackResponse> add(String tid){

        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TRACK_ADD_URL)
                .append("?")
                .append("key=" + key)
                .append("&")
                .append("sid=" + sid)
                .append("&")
                .append("tid=" + tid);

        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null, String.class);

        String body = forEntity.getBody();
        JSONObject result = JSONObject.fromObject(body);
        JSONObject data = result.getJSONObject("data");
        String trid = data.getString("trid");
        String trname = data.has("trname") ? data.getString("trname") : "";

        TrackResponse trackResponse = new TrackResponse();
        trackResponse.setTrid(trid);
        trackResponse.setTrname(trname);
        return ResponseResult.success(trackResponse);
    }
}
