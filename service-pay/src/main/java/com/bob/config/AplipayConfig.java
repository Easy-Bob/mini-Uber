package com.bob.config;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AplipayConfig {
    private String appId;
    private String appPrivateKey;
    private String publicKey;
    private String notifyUrl;

    @PostConstruct
    public void init(){
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "https://openapi.alipaydev.com";
        config.signType = "RSA2";

        config.appId = this.appId;
        config.merchantPrivateKey = this.appPrivateKey;
        config.alipayPublicKey = this.publicKey;
        config.notifyUrl = notifyUrl;

        Factory.setOptions(config);
        System.out.println("Alipay init success");
    }
}
