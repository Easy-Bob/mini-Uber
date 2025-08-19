package com.bob.serviceprice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.dto.PriceRule;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceprice.mapper.PriceRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sun on 8/17/2025.
 * Description:
 */
@Service
public class PriceRuleService {

    @Autowired
    private PriceRuleMapper priceRuleMapper;

    public ResponseResult add(PriceRule priceRule) {
        System.out.println(priceRule);
        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        String fareType = cityCode + "$" +vehicleType;
        priceRule.setFareType(fareType);

        // 添加版本号
        Map<String, Object> map = new HashMap<>();

        // 因为版本号不删除，所以会有多个版本号，查找最大的那个
        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> priceRules = priceRuleMapper.selectList(queryWrapper);
        Integer fareVersion = 0;
        if(priceRules != null && priceRules.size() > 0){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EXISTS.getCode(), CommonStatusEnum.PRICE_RULE_EXISTS.getValue());
        }
        priceRule.setFareVersion(++fareVersion);

        priceRuleMapper.insert(priceRule);
        return ResponseResult.success("");
    }

    public ResponseResult edit(PriceRule priceRule) {
        System.out.println(priceRule);
        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        String fareType = cityCode + "$" +vehicleType;
        priceRule.setFareType(fareType);

        // 添加版本号
        Map<String, Object> map = new HashMap<>();

        // 因为版本号不删除，所以会有多个版本号，查找最大的那个
        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> priceRules = priceRuleMapper.selectList(queryWrapper);
        Integer fareVersion = 0;
        if(priceRules != null && priceRules.size() > 0){
            PriceRule lastestPriceRule = priceRules.get(0);
            fareVersion = lastestPriceRule.getFareVersion();
            if(priceRule.equals(lastestPriceRule)){
                return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EXISTS.getCode(), CommonStatusEnum.PRICE_RULE_EXISTS.getValue());
            }else{
                priceRule.setFareVersion(++fareVersion);
                priceRuleMapper.insert(priceRule);
                return ResponseResult.success("");
            }
        }
        return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(), CommonStatusEnum.PRICE_RULE_EMPTY.getValue());
    }
}
