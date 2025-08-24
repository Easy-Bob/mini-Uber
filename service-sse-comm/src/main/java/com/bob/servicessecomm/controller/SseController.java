package com.bob.servicessecomm.controller;

import com.bob.internalcommon.constant.util.SsePrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class SseController {

    public static Map<String, SseEmitter> sseEmitterMap = new HashMap<>();

    /**
     * 建立连接
     * @param userId
     * @param identity
     * @return
     */
    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam Long userId, @RequestParam String identity){
        log.info("User id: " + userId + ", identity = " + identity);
        String sseMapKey = SsePrefixUtils.generateSseKey(userId, identity);
        SseEmitter sseEmitter = new SseEmitter(0L);
        sseEmitterMap.put(sseMapKey, sseEmitter);
        return sseEmitter;
    }

    /**
     * 发送消息
     * @param userId
     * @param identity
     * @param content
     * @return
     */
    @GetMapping("/push")
    public String push(@RequestParam Long userId, @RequestParam String identity, @RequestParam String content){
        String sseMapKey = SsePrefixUtils.generateSseKey(userId, identity);
        try {
            if(sseEmitterMap.containsKey(sseMapKey)){
                sseEmitterMap.get(sseMapKey).send(content);
            }else{
                return "推送失败";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "给用户：" + sseMapKey + ", 发送了消息" + content;
    }

    /**
     * 关闭连接
     * @param userId
     * @param identity
     * @return
     */

    @GetMapping("/close")
    public String close(@RequestParam Long userId, @RequestParam String identity){
        String sseMapKey = SsePrefixUtils.generateSseKey(userId, identity);
        System.out.println("关闭连接：" + sseMapKey);
        if(sseEmitterMap != null && sseEmitterMap.containsKey(sseMapKey)){
            sseEmitterMap.remove(sseMapKey);
        }
        return "close success";
    }
}
