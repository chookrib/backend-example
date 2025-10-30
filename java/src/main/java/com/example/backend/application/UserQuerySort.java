package com.example.backend.application;

/**
 * 用户 Query Sort
 */
public enum UserQuerySort {

    CREATED_AT_ASC(1, "创建时间升序"),
    CREATED_AT_DESC(-1, "创建时间降序"),
    USERNAME_ASC(2, "用户名升序"),
    USERNAME_DESC(-2, "用户名降序");

    private int value;      //值
    private String text;    //文本

    public int getValue() { return value; }

    public String getText() { return text; }

    UserQuerySort(int value, String text) {
        this.value = value;
        this.text = text;
    }
}
