package com.fly.flyaiinterface;


import com.fly.flyapiclientsdk.client.FlyApiClient;
import com.fly.flyapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

@SpringBootTest
class FlyaiInterfaceApplicationTests {

    @Resource
    private FlyApiClient flyApiClient;

    @Test
    void contextLoads() throws UnsupportedEncodingException {
/*        String name = flyApiClient.getNameByGet("fly");
        System.out.println(name);
        */
        User user = new User();
        user.setUsername("fly");
        String userNameByPost = flyApiClient.getNameByPostWithJson(user);
        System.out.println(userNameByPost);
    }

}
