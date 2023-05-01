package com.fly.flyaiinterface;


import com.fly.flyapiclientsdk.client.FlyApiClient;
import com.fly.flyapiclientsdk.model.User;

import java.io.UnsupportedEncodingException;

public class MainTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String accessKey = "fly";
        String secretKey = "abcdef";

        FlyApiClient flyApiClient = new FlyApiClient(accessKey, secretKey);

        String ss = flyApiClient.getNameByGet("ss");
        System.out.println(ss);

        String ee = flyApiClient.getNameByPost("ee");
        System.out.println(ee);

        User user = new User();
        user.setUsername("dd");
        String userNameByPost = flyApiClient.getNameByPostWithJson(user);
        System.out.println(userNameByPost);
    }
}
