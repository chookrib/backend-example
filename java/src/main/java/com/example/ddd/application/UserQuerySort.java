package com.example.ddd.application;

/**
 * 用户查询Sort
 */
public enum UserQuerySort {
    CreatedAtAsc(1, "创建时间升序"),
    CreatedAtDesc(-1, "创建时间倒序"),
    UsernameAsc(2, "用户名升序"),
    UsernameDesc(-2, "用户名降序");

    private int value;      //值
    private String text;    //文本

    public int getValue() { return value; }

    public String getText() { return text; }

    UserQuerySort(int value, String text) {
        this.value = value;
        this.text = text;
    }
}
