package com.bob.servicemap.service;

import com.bob.internalcommon.constant.constant.AmapConfigConstants;
import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.dto.DicDistrict;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.servicemap.mapper.DicDistrictMapper;
import com.bob.servicemap.remote.MapDistrictClient;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 2025/8/11.
 * Description:
 */
@Service
@Slf4j
public class DicDistrictService {

    @Autowired
    private MapDistrictClient mapDistrictClient;

    @Autowired
    private DicDistrictMapper dicDistrictMapper;

    public ResponseResult initDicDistrict(String keywords){
        // 请求地图
        String dicDistrictResult = mapDistrictClient.dicDistrict(keywords);
        // 解析结果
        JSONObject dicDistrictJsonObject = JSONObject.fromObject(dicDistrictResult);
        int status = dicDistrictJsonObject.getInt(AmapConfigConstants.STATUS);

        if(status != 1){
            return ResponseResult.fail(CommonStatusEnum.MAP_DISTRICT_ERROR);
        }

        JSONArray countryJSONArray = dicDistrictJsonObject.getJSONArray(AmapConfigConstants.DISTRICTS);

        // 递归插入数据库
        traverseJSON(countryJSONArray, "0");

        return ResponseResult.success();
    }

    // 遍历json, 插入数据库
    private void traverseJSON(JSONArray jsonArray, String parentAddressCode){
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String addressCode = jsonObject.getString(AmapConfigConstants.ADCODE);
            String addressName = jsonObject.getString(AmapConfigConstants.NAME);
            String level = jsonObject.getString(AmapConfigConstants.LEVEL);
            int levelInt = generateLevel(level);

            if(levelInt == -1){
                continue;
            }
            insertDicDistrict(addressCode,addressName,levelInt,parentAddressCode);

            if(levelInt < 3){
                // recursive traversal
                traverseJSON(jsonObject.getJSONArray(AmapConfigConstants.DISTRICTS), addressCode);
            }
        }
    }


    private void insertDicDistrict(String addressCode, String addressName, Integer levelInt, String parentAddressCode){
        // 数据库对象
        DicDistrict dicDistrict = new DicDistrict();
        dicDistrict.setAddressCode(addressCode);
        dicDistrict.setAddressName(addressName);
        dicDistrict.setParentAddressCode(parentAddressCode);
        dicDistrict.setLevel(levelInt);

        // 插入数据库
        dicDistrictMapper.insert(dicDistrict);
    }


    private int generateLevel(String level){
        if(level == null){
            return -1;
        }
        if(level.trim().equals("country")){
            return 0;
        }else if(level.trim().equals("province")){
            return 1;
        }else if(level.trim().equals("city")){
            return 2;
        }else if(level.trim().equals("district")){
            return 3;
        }else{
            return -1;
        }
    }
}
