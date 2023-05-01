package com.fly.flyaiinterface.controller;

import com.fly.flyapiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称API
 */
@RestController
@RequestMapping( "/name" )
public class NameController {
    @GetMapping( "/" )
    public String getNameGet(String name, HttpServletRequest request) {
        System.out.println(request.getHeader("fly"));
        return "get 姓名:" + name;
    }

    @PostMapping( "/post" )
    public String getNamePost(@RequestParam String name) {
        return "post: name" + name;
    }

    @PostMapping( "/user" )
    public String getNameByPostWithJson(@RequestBody User user, HttpServletRequest request)  {
        String result = "发送POST请求 JSON中你的名字是：" + user.getUsername();
        // 接口次数+1
        return result;
    }

}
