package com.kernel.intelcurrent.modeladapter;

import com.kernel.intelcurrent.model.ICModel;
import com.kernel.intelcurrent.model.Task;

/**
 * classname:ModelAdapter.java
 * @author ������
 * */
public abstract class ModelAdapter extends Thread 
{
    Task task;
    ICModel model;
    
    public ModelAdapter(Task t)
    {
    	task=t;
    	model=ICModel.getICModel();
    }
    /**
     * ������ʵ��run������ִ������ͬһ����������һ���߳����ʱ��Ҫ�ص�model��callback����
     * */
    public abstract void run();
}
