package com.bob.apipassenger.controller;

import com.bob.apipassenger.service.ServiceOrderService;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/16.
 * Description:
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ServiceOrderService serviceOrderService;

    /**
     * 创建订单/下单
     * @return
     */
    @PostMapping("/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest){

        return serviceOrderService.add(orderRequest);
    }
}
