package com.bob.serviceorder.remote;

import com.bob.internalcommon.constant.request.PushRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient("service-sse-push")
public interface ServiceSseCommClient {

    @PostMapping(value = "/push")
    public String push(@RequestBody PushRequest pushRequest);
}
