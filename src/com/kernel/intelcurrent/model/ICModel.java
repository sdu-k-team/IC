package com.kernel.intelcurrent.model;

import java.util.LinkedList;

import com.kernel.intelcurrent.service.MainService;

/**
 * classname:ICModel.java
 * @author ������
 * */
public class ICModel 
{
   private static final String TAG=ICModel.class.getSimpleName();
   private static ICModel model;
   private LinkedList<Task> tasks;
   private LinkedList<Group> group;
   private boolean sinaBound=false,
		           tencentBound=false;
   private int sinaAccessToken,tencentAccessToken;
   
   private ICModel()
   {
	   tasks=new LinkedList<Task>();
   }
   public static ICModel getICModel()
   {
	   if(model==null)
	   {
		   model=new ICModel();
	   }
	   return model;
   }
   /**
    * ����ִ�У�service���ø÷���ִ������
    * */
   public void doTask(Task task)
   {
	   tasks.add(task);
	   int type=task.type;
	   int total=checkForThreadsNum(type);
	   task.total=total;
	   switch(type)
	   {
	   //����������ִ��
	   }
   }
   public int checkForThreadsNum(int type)
   {
	   int result=1;
	   //�����߼��ж�
	   return result;
   }
   /**
    * ���߳��������ʱ����callback�����ص�
    * */
   public void callBack(Task task)
   {
	   //deal with the result
	   //...
	   
	   MainService service=MainService.getInstance();
	   service.send(task);
   }
   
}
