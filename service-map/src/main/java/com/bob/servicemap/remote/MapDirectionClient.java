package com.bob.servicemap.remote;

import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.response.DirectionResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */
@Service
@Slf4j
public class MapDirectionClient {

    @Value("${amap.key}")
    private String amapKey;

    @Autowired
    private RestTemplate restTemplate;

    public DirectionResponse direction(String depLongitude, String depLatitude, String destLongitude, String destLatitude){
        // 组装请求调用的url

//        https://restapi.amap.com/v5/direction/driving?origin=116.434307,39.90909&destination=116.434446,39.90816&key=<用户的key>

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(AmapConfigConstants.DIRECTION_URL).append("?");
        urlBuilder.append("origin=" + depLongitude + ","            + depLatitude)
                .append("&")
                .append("destination=" + destLongitude + "," + destLatitude)
                .append("&")
                .append("extensions=base")
                .append("&")
                .append("output=json")
                .append("&")
                .append("key=")
                .append(amapKey);

//        log.info(urlBuilder.toString());
        //调用高德接口
        ResponseEntity<String> directionEntity = restTemplate.getForEntity(urlBuilder.toString(), String.class);
        String directionString = directionEntity.getBody();
        log.info("高德地图，路径规划，返回信息：" + directionEntity);

        // 解析接口
        DirectionResponse directionResponse = parseDirectionEntity(directionString);
//        log.info(directionResponse.toString());
        return directionResponse;
    }

    private DirectionResponse parseDirectionEntity(String directionString){
        DirectionResponse directionResponse = null;
        try{
            // 最外层
            JSONObject result = JSONObject.fromObject(directionString);
            // 解析json串
            if(result.has(AmapConfigConstants.STATUS)
                    && result.getInt(AmapConfigConstants.STATUS) == 1){
                if(result.has(AmapConfigConstants.ROUTE)){
                    JSONObject routeObject = result.getJSONObject(AmapConfigConstants.ROUTE);
                    JSONArray pathsArray = routeObject.getJSONArray(AmapConfigConstants.PATHS);
                    JSONObject pathObject = pathsArray.getJSONObject(0);

                    directionResponse = new DirectionResponse();

                    if(pathObject.has(AmapConfigConstants.DISTANCE)){
                        int distance = pathObject.getInt(AmapConfigConstants.DISTANCE);
                        directionResponse.setDistance(distance);
                    }
                    if(pathObject.has(AmapConfigConstants.DURATION)){
                        int duration = pathObject.getInt(AmapConfigConstants.DURATION);
                        directionResponse.setDuration(duration);
                    }
                }
            }
        }catch (Exception e){

        }
        return directionResponse;

    }

}
