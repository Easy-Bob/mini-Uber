package com.bob.servicemap.remote;

import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.dto.PointDTO;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.PointRequest;
import com.bob.internalcommon.constant.response.TrackResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@Service
public class PointClient {

    @Value("${amap.key}")
    private String key;

    @Value("${amap.sid}")
    private String sid;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult upload(PointRequest pointRequest){

        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TRACK_UPLOAD_URL)
                .append("?")
                .append("key=" + key)
                .append("&")
                .append("sid=" + sid)
                .append("&")
                .append("tid=" + pointRequest.getTid())
                .append("&")
                .append("trid=" + pointRequest.getTrid())
                .append("&")
                .append("points=");
        PointDTO[] points = pointRequest.getPoints();
        JSONArray pointsArray = new JSONArray();
        for (PointDTO p : pointRequest.getPoints()) {
            JSONObject pointJson = new JSONObject();
            pointJson.put("location", p.getLocation());
            pointJson.put("locatetime", p.getLocatetime());
            pointsArray.add(pointJson);
        }
        String pointsJson = pointsArray.toString();
// 关键点：对整个 JSON 字符串进行 URL 编码
        url.append(URLEncoder.encode(pointsJson));

        System.out.println(url.toString());
        ResponseEntity<String> forEntity = restTemplate.postForEntity(URI.create(url.toString()), null, String.class);
        String body = forEntity.getBody();
//        System.out.println(body);
        return ResponseResult.success("");
    }
}
