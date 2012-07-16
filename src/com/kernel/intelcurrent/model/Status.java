﻿package com.kernel.intelcurrent.model;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

/**
 * classname:Status.java
 * @author 许凌霄
 * */
public class Status 
{
    private static final String TAG=Status.class.getSimpleName();
    /**
     * 微博id
     * */
    public String id;
    
    /**
     * 文本内容
     * */
    public String text;
    
    /**
     * 微博来源
     * */
    public String source;
    
    /**
     * 图片url列表
     * */
    public ArrayList<String> image=new ArrayList<String>();
    
    /**
     * 微博发布时间戳
     * */
    public int timestamp;
    
    /**
     *回复数 
     * */
    public int rCount;
    
    /**
     * 评论数
     * */
    public int cCount;
    
    /**
     * 地理信息对象
     * */
    public GeoInfo geo=new GeoInfo();
    
    /**
     * 用户对象
     * */
    public User user=new User();
    
    /**
     * 微博发布平台
     * */
    public String platform;
}
