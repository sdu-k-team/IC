package com.kernel.intelcurrent.model;
/**
 * classname:Status.java
 * @author ������
 * */
public class Status 
{
    private static final String TAG=Status.class.getSimpleName();
    /**
     * ΢��id
     * */
    public long id;
    
    /**
     * �ı�����
     * */
    public String text;
    
    /**
     * ΢����Դ
     * */
    public String source;
    
    /**
     * ͼƬurl�б�
     * */
    public String[] image;
    
    /**
     * ΢������ʱ���
     * */
    public int timestamp;
    
    /**
     *�ظ��� 
     * */
    public int rCount;
    
    /**
     * ������
     * */
    public int cCount;
    
    /**
     * ������Ϣ����
     * */
    public GeoInfo geo;
    
    /**
     * �û�����
     * */
    public User user;
    
    /**
     * ΢������ƽ̨
     * */
    public String platform;
}
