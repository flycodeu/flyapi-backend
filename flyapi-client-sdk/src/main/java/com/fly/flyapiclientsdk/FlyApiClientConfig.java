package com.fly.flyapiclientsdk;

import com.fly.flyapiclientsdk.client.FlyApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties( "flyapi.client" )
@Data
@ComponentScan
public class FlyApiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public FlyApiClient flyApiClient() {
        return  new FlyApiClient(accessKey, secretKey);
    }
}
