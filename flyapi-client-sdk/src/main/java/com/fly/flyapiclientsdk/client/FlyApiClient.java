package com.fly.flyapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fly.flyapiclientsdk.model.User;
import com.fly.flyapiclientsdk.utils.SignUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用第三方接口客户端
 */
public class FlyApiClient {
    private String accessKey;
    private String secretKey;
    public static final String GATEWAY_HOST = "http://localhost:8090";

    public FlyApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result3 = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result3);
        return result3;
    }


    public String getNameByPost(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result3 = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result3);
        return result3;
    }

    private Map<String, String> getHeaders(String body) throws UnsupportedEncodingException {
        Map<String, String> header = new HashMap<>();
        header.put("accessKey", accessKey);
        header.put("sign", SignUtils.getSign(body, secretKey));
        // 防止中文乱码
        header.put("body", URLEncoder.encode(body, StandardCharsets.UTF_8.name()));
        header.put("nonce", RandomUtil.randomNumbers(5));
        header.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return header;
    }

    /**
     * 获取用户姓名
     * @param user
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getNameByPostWithJson(User user) throws UnsupportedEncodingException {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse response = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaders(json))
                .body(json)
                .execute();
        // System.out.println("response = " + response);
        System.out.println("status = " + response.getStatus());
        if (response.isOk()) {
            return response.body();
        }
        return "fail";
    }

    public String getFunnyStory() throws UnsupportedEncodingException {
        HttpResponse response = HttpRequest.get(GATEWAY_HOST + "/api/story/getStory")
                .addHeaders(getHeaders(""))
                .execute();
        if (response.isOk()) {
            return response.body();
        }
        return "fail";
    }

}
