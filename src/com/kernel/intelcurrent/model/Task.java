package com.kernel.intelcurrent.model;

import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
/**
 * classname:Task.java
 * @author ������
 * */
public class Task 
{
	private static final String TAG=Task.class.getSimpleName();
	
	/**
	 * ��������
	 * */
	public int type;
	
	/**
	 * �߳�����
	 * */
	public int total;
	
	/**
	 * Ŀǰ�ظ�������߳���
	 * */
	public int current;
	
	/**
	 * ����Ŀ��activity
	 * */
	public Activity target;
	
	/**
	 * ��������
	 * */
	public LinkedList<Object> result;
	
	/**
	 * ���������
	 * */
    private Map<String,Object> param;
    public Task(int t,Map<String,Object> p,Activity target)
    {
    	type=t;
    	param=p;
    	this.target=target;
    }
}
