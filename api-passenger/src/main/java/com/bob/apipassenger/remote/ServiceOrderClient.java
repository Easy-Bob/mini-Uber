package com.bob.apipassenger.remote;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.OrderRequest;
import org.aspectj.weaver.ast.Or;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Sun on 8/17/2025.
 * Description:
 */
@FeignClient("service-order")
public interface ServiceOrderClient {

    @RequestMapping(method = RequestMethod.POST, value = "/order/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest);
}
