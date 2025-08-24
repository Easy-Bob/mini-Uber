package com.bob.apiDriver.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-sse-comm")
public interface ServiceSseCommClient {

    @RequestMapping(method = RequestMethod.POST, value = "/push")
    public String push(@RequestParam Long userId, @RequestParam String identity, @RequestParam String content);
}
