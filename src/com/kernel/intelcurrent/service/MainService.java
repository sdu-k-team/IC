﻿package com.kernel.intelcurrent.service;
import java.util.HashMap;
import java.util.Map;

import com.kernel.intelcurrent.activity.Updateable;
import com.kernel.intelcurrent.model.Group;
import com.kernel.intelcurrent.model.ICModel;
import com.kernel.intelcurrent.model.OAuthManager;
import com.kernel.intelcurrent.model.Task;
import com.kernel.intelcurrent.model.User;
import com.tencent.weibo.oauthv2.OAuthV2;

import android.app.*;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
/**
 * classname:MainService.java
 * @author 许凌霄
 * */
public class MainService extends Service
{
    private static final String TAG=MainService.class.getSimpleName();
    private static MainService service;
    private ICModel model;
    private IBinder mBinder=new ICBinder();
    private Activity currentActivity;
    
	@Override
	public void onCreate() {
    	service=this;
    	model=ICModel.getICModel();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}
	public static MainService getInstance()
	{
		return service;
	}
	public void changeCurrentActivity(Activity current)
	{
		currentActivity=current;
	}
	
	/*=======================操纵ICModel的方法开始=======================================*/
	
	/**得到Timeline的信息
	 * @author sheling*/
	public void getTimeline(Group group,int pageflag, long pagetime,String lastid){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("pageflag", pageflag);
		map.put("pagetime", pagetime);
		map.put("lastid", lastid);
		map.put("reqnum", 25);
		
		StringBuilder openids = new StringBuilder();
		for(User user:group.users){
			openids.append(user.id).append("_");
		}
		map.put("fopenids", openids.toString());
		Task t = new Task(Task.G_GET_GROUP_TIMELINE,map,null);
		model.doTask(t,this);
	}
	
	/**
	 * 获取用户的信息 //fopenids为0为用户自己（腾讯）
	 * @author allenjin
	 */
	public void getUserInfo(String openid){
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("openid", openid);
			Task t=new Task(Task.USER_INFO,map,null);
			model.doTask(t, this);
			Log.v(TAG,"getuserinfo in service");
	}
	/**
	 * 获取提及用户的微博列表（包括：at和评论）
	 * @author allenjin
	 * @param type:0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评，若为0则为全部获取
	 */
	public void getMentionWeiboList(int type,int pageflag, long pagetime,String lastid){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("type", type);
		map.put("pageflag", pageflag);
		map.put("pagetime", pagetime);
		map.put("lastid", lastid);
		map.put("reqnum", 20);
		Task t;
		if(type==(0x8|0x40)){
			t=new Task(Task.MSG_COMMENTS_ME_LIST,map,null);//评论
		}else{
			t=new Task(Task.MSG_COMMENTS_MENTIONS,map,null);
		}
		model.doTask(t, this);
	}
	/**
	 * 获取私信内容
	 * @author allenjin
	 */
	public void getPriMsgList(int pageflag, long pagetime,String lastid){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("pageflag", pageflag);
		map.put("pagetime", pagetime);
		map.put("lastid", lastid);
		map.put("reqnum", 20);
		Task t=new Task(Task.MSG_PRIVATE_LIST,map,null);
		model.doTask(t, this);
	}
	/**
	 * 获取用户自己的粉丝列表
	 * @param startindex 填当前获取的为第几页 第一页为:1;
	 */
	public void getUserFansList(int startindex){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("reqnum", 20);
		map.put("startindex",20*(startindex-1));
		Task t=new Task(Task.USER_FANS_LIST,map,null);
		model.doTask(t, this);
	}
	/**
	 * 获取用户自己的收听列表
	 * @param startindex 填当前获取的为第几页 第一页为:1;
	 */
	public void getUserFriendsList(int startindex){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("reqnum", 20);
		map.put("startindex",20*(startindex-1));
		Task t=new Task(Task.USER_FRIENDS_LIST,map,null);
		model.doTask(t, this);
	}
	/**
	 * 获取用户的收藏列表
	 * @author allenjin
	 */
	public void getUserShoucangList(int pageflag, long pagetime,String lastid){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("pageflag", pageflag);
		map.put("pagetime", pagetime);
		map.put("lastid", lastid);
		map.put("reqnum", 20);
		Task t=new Task(Task.USER_FAV_LIST,map,null);
		model.doTask(t, this);
	}
	/**
	 * 获取用户自己发表的微博列表
	 * @author allenjin
	 */
	public void getUserWeiboList(int pageflag, long pagetime,String lastid){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("pageflag", pageflag);
		map.put("pagetime", pagetime);
		map.put("lastid", lastid);
		map.put("reqnum", 20);
		Task t=new Task(Task.USER_WEIBO_LIST,map,null);
		model.doTask(t, this);
	}
	/**
	 * 获取其他用户的听众列表
	 * @param name	用户名
	 * @param startindex	填当前获取的为第几页 第一页为:1;
	 */
	public void getOtherFansList(String name,int startindex){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("reqnum", 20);
		map.put("name", name);
		map.put("startindex",20*(startindex-1));
		Task t=new Task(Task.USER_OTHER_FANS_LIST,map,null);
		model.doTask(t, this);
	}
	/**
	 * 获取其他用户的收听列表
	 * @param name 用户名
	 * @param startindex	填当前获取的为第几页 第一页为:1;
	 */
	public void getOtherFollowList(String name,int startindex){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("reqnum", 20);
		map.put("startindex",20*(startindex-1));
		map.put("name", name);
		Task t=new Task(Task.USER_OTHER_FRIENDS_LIST,map,null);
		model.doTask(t, this);
	}
	/**
	 * 获取其他用户发表的微博列表
	 * @param fopenids 其他用户的openid
	 * @author allenjin
	 */
	public void getOtherWeiboList(String fopenids,int pageflag, long pagetime,String lastid){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("pageflag", pageflag);
		map.put("pagetime", pagetime);
		map.put("fopenids", fopenids);
		map.put("lastid", lastid);
		map.put("reqnum", 20);
		Task t=new Task(Task.USER_OTHER_WEIBO_LIST,map,null);
		model.doTask(t, this);
	}
	/**
	 * 取消收听用户
	 * @param name 用户名
	 * @author allenjin
	 */
	public void delFriend(String name){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);
		Task t=new Task(Task.USER_FREINDS_DEL,map,null);
		model.doTask(t, this);
	}
	/**
	 * 收听用户
	 * @param name 用户名
	 * @author allenjin
	 */
	public void addFriend(String name){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);
		Task t=new Task(Task.USER_FRIENDS_ADD,map,null);
		model.doTask(t, this);
	}
	/**
	 * 获取其他用户信息
	 * @param openid 其他用户的openid
	 * @author allenjin
	 */
	public void getOtherUserInfo(String openid){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("openid", openid);
		Task t=new Task(Task.USER_OTHER_INFO,map,null);
		model.doTask(t, this);
		Log.v(TAG,"getuserinfo in service");
	}
	/**添加一条微博*/
	public void addWeibo(String content,String imgUrl,int platform){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("content", content);
		map.put("imgurl", imgUrl);
		map.put("platform", platform);
		Task t = new Task(Task.WEIBO_ADD,map,null);
		model.doTask(t, this);
		Log.v(TAG, "add weibo"+content+"img"+imgUrl);
	}
	
	/**添加一条评论*/
	public void addComment(String content,String reid,int platform){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("content", content);
		map.put("reid", reid);
		map.put("platform", platform);
		Task t = new Task(Task.WEIBO_COMMENTS_ADD,map,null);
		model.doTask(t, this);
	}
	
	/**转发一条微博*/
	public void rePost(String content,String reid,int platform){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("content", content);
		map.put("reid", reid);
		map.put("platform", platform);
		Task t = new Task(Task.WEIBO_REPOST,map,null);
		model.doTask(t, this);
	}
	
	/**获取评论列表
	 * 
	 *@param lastid  1-100为0，之后填上一个的id*/
	public void getCommentList(String rootid,int pageflag,long pagetime,String lastid,int platform){
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("flag", 1);
		map.put("rootid", rootid);
		map.put("pageflag", pageflag);
		map.put("pagetime", pagetime);
		map.put("reqnum", 10);
		map.put("twitterid", lastid);
		map.put("platform",platform);
		Task t = new Task(Task.WEIBO_COMMENTS_BY_ID,map,null);
		model.doTask(t, this);
	}
	/*=======================操纵ICModel的方法结束=======================================*/
	
	/**
	 * inner class:ICBinder
	 * 
	 * */
    public class ICBinder extends Binder
    {
    	/**
    	 * 让activity 获得MainService实例
    	 * */
    	public MainService getService()
 	   {
 	      return MainService.this;
 	   }
    }
    public void send(Task task)
    {
    	Message msg=new Message();
    	msg.obj=task;
    	msg.what=task.type;
    	handler.sendMessage(msg);
    }
    /**
     * 处理底层消息的内部类
     * */
    private Handler handler=new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		((Updateable)currentActivity).update(msg.what,msg.obj);
    	}
    };
}
