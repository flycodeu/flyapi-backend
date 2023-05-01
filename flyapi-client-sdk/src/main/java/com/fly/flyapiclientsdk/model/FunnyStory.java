package com.fly.flyapiclientsdk.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FunnyStory implements Serializable {

    private static final long serialVersionUID = 4879099175922223820L;
    private String title;
    private String content;
}
