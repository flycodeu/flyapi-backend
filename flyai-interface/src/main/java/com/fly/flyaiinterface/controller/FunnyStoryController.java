package com.fly.flyaiinterface.controller;

import com.fly.flyapiclientsdk.model.FunnyStory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping( "/story" )
@RestController()
public class FunnyStoryController {

    @GetMapping( "/getStory" )
    public String getStory() {
        String title = "你好";
        String content = "hello";
        FunnyStory funnyStory = new FunnyStory();
        funnyStory.setTitle(title);
        funnyStory.setContent(content);
        StringBuilder stringBuilder =new StringBuilder();
        StringBuilder builder = stringBuilder.append("标题:").append(title).append("内容:").append(content);
        return builder.toString();
    }
}
