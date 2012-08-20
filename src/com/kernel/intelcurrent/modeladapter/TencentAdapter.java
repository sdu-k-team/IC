package com.kernel.intelcurrent.modeladapter;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.kernel.intelcurrent.model.Comment;
import com.kernel.intelcurrent.model.ErrorEntry;
import com.kernel.intelcurrent.model.ICArrayList;
import com.kernel.intelcurrent.model.Status;
import com.kernel.intelcurrent.model.Task;
import com.kernel.intelcurrent.model.User;
import com.tencent.weibo.api.FavAPI;
import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.InfoAPI;
import com.tencent.weibo.api.PrivateAPI;
import com.tencent.weibo.api.SearchAPI;
import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.beans.OAuth;

/**腾讯的adapter
 * @author sheling*/
public class TencentAdapter extends ModelAdapter {

	private static final String TAG = TencentAdapter.class.getSimpleName();
	
	private ErrorEntry error;
	
	public TencentAdapter(Task t) {
		super(t);
		error = new ErrorEntry();
		error.platform = User.PLATFORM_TENCENT_CODE;
	}

	@Override
	public void run() {
		try{
			switch(task.type){
			case Task.G_GET_GROUP_TIMELINE:
				getOtherWeiboList();
				break;
			case Task.USER_INFO:
			case Task.USER_OTHER_INFO:
				getUserInfo();
				break;
			case Task.WEIBO_ADD:
				addWeibo();
				break;
			case Task.WEIBO_COMMENTS_ADD:
				addComment();
				break;
			case Task.WEIBO_REPOST:
				repostWeibo();
				break;
			case Task.WEIBO_COMMENTS_RE:
				replyWeibo();
				break;
			case Task.MSG_COMMENTS_MENTIONS:
			case Task.MSG_COMMENTS_ME_LIST:
				getMentionsWeiboList();
				break;
			case Task.MSG_PRIVATE_LIST:
				getPrivateMsgList();
				break;
			case Task.USER_FANS_LIST:
				getUserList(1);
				break;
			case Task.USER_FAV_LIST:
				getFavWeiboList();
				break;
			case Task.USER_FRIENDS_LIST:
				getUserList(2);
				break;
			case Task.USER_WEIBO_LIST:
				getUserWeiboList();
				break;
			case Task.WEIBO_COMMENTS_BY_ID:
				getCommentList();
				break;
			case Task.USER_FREINDS_DEL:
				delFriend();
				break;
			case Task.USER_FRIENDS_ADD:
				addFriend();
				break;
			case Task.USER_OTHER_FANS_LIST:
				getOtherFanList();
				break;
			case Task.USER_OTHER_FRIENDS_LIST:
				getOtherIdolList();
				break;
			case Task.USER_OTHER_WEIBO_LIST:
				getOtherWeiboList();
				break;
			case Task.WEIBO_ADD_AT:
				searchUser();
				break;
			case Task.USER_SIMPLE_INFO_LIST:
				getSimUserInfoList();
				break;
			case Task.USER_SEARCH:
				searchUser();
				break;
			case Task.USER_HOME_TIMELINE_LIST:
				getHomeTimeLine();
				break;
			}
		} catch(SSLException e){
			Log.e(TAG, "exception: "+e.toString());
			error.exception = ErrorEntry.EXCEPTION_SSL;
		} catch (ConnectTimeoutException e){
			Log.e(TAG, "exception: "+e.toString());
			error.exception = ErrorEntry.EXCEPTION_CONNECT_TIME_OUT;
		} catch (UnknownHostException e){
			Log.e(TAG, "exception: "+e.toString());
			error.exception = ErrorEntry.EXCEPTION_UNKNOWN_HOST;
		} catch (JSONException e){
			Log.e(TAG, "exception: "+e.toString());
			error.exception = ErrorEntry.EXCEPTION_JSON;
		} catch(Exception e){
			Log.e(TAG, "exception: "+e.toString());
			error.exception = ErrorEntry.EXCEPTION_OTHER;
		}
		//添加进结果
		task.errors.put(error.platform, error);
		model.callBack(task);
	}

	/**添加一条不带图片的微博
	 * Map参数：content：微博内容,clientip：客户端IP地址 imgurl:图片地址(如果有的话)
	 * @author sheling
	 * @throws Exception */
	public String addWeibo() throws Exception{
		TAPI tapi = new TAPI();
		Map<String, Object> map = task.param;
		String response = null;
		Log.d(TAG, "params: "+map.toString());
		if(map.get("imgurl") == null){
			response = tapi.add((OAuth)map.get("oauth"), "json",
					map.get("content").toString(),
					map.get("clientip").toString());
		}else{
			response=tapi.addPic((OAuth)map.get("oauth"), "json",map.get("content").toString(),
					map.get("clientip").toString(),map.get("imgurl").toString());
		}
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		tapi.shutdownConnection();
		task.result.add(response);
		Log.d(TAG, response);
		return response;
	}
	
//	/**
//	 * 添加一条带图片的微博
//	 * Map参数:content:微博文本内容,clientip:客户端IP,imgurl:图片的存储路径
//	 * @author allenjin
//	 */
//	public void addpicWeibo(){
//		TAPI tapi=new TAPI();
//		Map<String,Object> map=task.param;
//		String response=null;
//		try{
//			
//			Log.v(TAG,response);
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		task.result.add(response);
//		tapi.shutdownConnection();
//	}

	/**
	 * 获取一条微博的详细信息,通过微博ID
	 * Map参数：id 微博的id
	 * @author allenjin
	 * @throws Exception 
	 */
	public void showWeibo() throws Exception{
		TAPI tapi=new TAPI();
		Map<String,Object> map=task.param;
		String response= null;
		response=tapi.show((OAuth)map.get("oauth"),"json", (String) map.get("id"));
		
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		
		if(error.ret == 0){
			JSONObject data=new JSONObject(response).getJSONObject("data");
			Status s=new Status();
			s.user.id=data.getString("openid");
			s.user.head=data.getString("head");
			s.user.name=data.getString("name");
			s.user.nick=data.getString("nick");
			s.id=data.getString("id");
			s.cCount=data.getInt("count");
			s.rCount=data.getInt("mcount");
			s.source=data.getString("from");
			if(data.get("image")instanceof JSONArray){
				JSONArray image=data.getJSONArray("image");
				for(int i=0;i<image.length();i++){
					s.image.add(image.getString(i));
				}
			}
			if(data.get("source") instanceof JSONObject){
				JSONObject res=data.getJSONObject("source");
				Status reStatus=new Status();
				reStatus.cCount=res.getInt("count");
				reStatus.rCount=res.getInt("mcount");
				reStatus.id=res.getString("id");
				reStatus.source=res.getString("from");
				reStatus.platform=User.PLATFORM_TENCENT_CODE;
				reStatus.timestamp=res.getLong("timestamp");
				reStatus.text=res.getString("origtext");
				reStatus.user.head=res.getString("head");
				reStatus.user.id=res.getString("openid");
				reStatus.user.name=res.getString("name");
				reStatus.user.nick=res.getString("nick");
				if(res.get("image")instanceof JSONArray){
					JSONArray image=res.getJSONArray("image");
					for(int i=0;i<image.length();i++){
						reStatus.image.add(image.getString(i));
					}
				}
				s.reStatus=reStatus;
			}		
			s.platform=User.PLATFORM_TENCENT_CODE;
			s.text=data.getString("origtext");
			s.timestamp=data.getLong("timestamp");
			Log.v(TAG, s.toString());
			task.result.add(s);
		}
		
		tapi.shutdownConnection();
		
	}
	
	/**添加一条评论
	 * @see Map参数：content:评论的内容 ,clientip:客户端IP,reid:微博的ID
	 * @author sheling
	 * @throws JSONException */
	public String addComment() throws Exception{
		TAPI tapi = new TAPI();
		Map<String,Object> map = task.param;
		String response = null;
		response = tapi.comment((OAuth)map.get("oauth"), "json",
				map.get("content").toString(), map.get("clientip").toString(), 
				map.get("reid").toString());
		
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		tapi.shutdownConnection();
		task.result.add(response);
		return response;
	}
	/**
	 * 回复一条微博
	 * @see Map参数:content:回复的内容,clientip:客户端IP,reid:回复的微博ID
	 * @author allenjin
	 * @throws Exception 
	 */
	public void replyWeibo() throws Exception{
		TAPI tapi=new TAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=tapi.reply((OAuth)map.get("oauth"), "json", map.get("content").toString(),
				map.get("clientip").toString(), map.get("reid").toString());
		if(response == null) throw new ConnectTimeoutException();
		Log.v(TAG,response);
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		tapi.shutdownConnection();
		task.result.add(response);
	}
	
	/**转发一条微博
	 * @see Map参数: content:转发写的内容,clientip:客户端IP,reid:转发的微博的ID
	 * @author sheling
	 * @throws Exception */
	public String repostWeibo() throws Exception{
		TAPI tapi = new TAPI();
		Map<String,Object> map = task.param;
		String response = null;
		response = tapi.reAdd((OAuth)map.get("oauth"), "json",
				map.get("content").toString(), map.get("clientip").toString(),
				map.get("reid").toString());
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		tapi.shutdownConnection();
		task.result.add(response);
		return response;
	}
	/**
	 * 获取一批人的简单资料
	 * @see fopenids:需要读取的用户的openid列表,用下划线_隔开，(<=30);
	 * 		处理的结果为一个ArrayList<User>
	 * @author allenjin
	 * @throws Exception 
	 */
	public void getSimUserInfoList() throws Exception{
		UserAPI uapi=new UserAPI();
		Map<String,Object> map = task.param;
		String response = null;
		ArrayList<User> users = new ArrayList<User>();
		response=uapi.infos((OAuth)map.get("oauth"), "json",null,map.get("fopenids").toString());

		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		if(error.ret == 0){
			JSONObject data=new JSONObject(response).getJSONObject("data");
			if(data.get("info") instanceof JSONArray){
				JSONArray infolist=data.getJSONArray("info");
				for(int i=0;i<infolist.length();i++){
					JSONObject iobj=infolist.getJSONObject(i);
					User user=new User();
					user.name=iobj.getString("name");
					user.id=iobj.getString("openid");
					user.nick=iobj.getString("nick");
					user.head=iobj.getString("head");
					user.fansnum=iobj.getInt("fansnum");
					user.idolnum=iobj.getInt("idolnum");
					user.gender = iobj.getInt("sex");
					user.platform=User.PLATFORM_TENCENT_CODE;
					users.add(user);
				}
			}
		}
		task.result.add(users);
		uapi.shutdownConnection();
		Log.v(TAG, "getUserList:"+response);
	}
	/**得到用户的信息
	 * @see Map参数:openid:用户的openid(0为用户自己)
	 * 处理结果为一个User对象
	 *@author sheling
	 * @throws Exception */
	public void getUserInfo() throws Exception{
		UserAPI uapi = new UserAPI();
		Map<String,Object> map = task.param;
		String response = null,openid =null;
		JSONObject rawObj,infoObj = null;
		User me = null;
		openid = map.get("openid").toString();
		if(openid.equals("0")){
			response = uapi.info((OAuth)map.get("oauth"), "json");	
		}else{
			response = uapi.otherInfo((OAuth)map.get("oauth"), "json",null,openid);
		}
		
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		if(error.ret == 0){
			infoObj = json.getJSONObject("data");
			me = new User();
			me.id = infoObj.getString("openid");
			me.name=infoObj.getString("name");
			me.nick = infoObj.getString("nick");
			String province = infoObj.getString("province_code");
			if(province!=null && !province.equals("")) me.province = Integer.valueOf(province);
			String city = infoObj.getString("city_code");
			if(city !=null && !city.equals("")) me.city = Integer.valueOf(city);
			me.location = infoObj.getString("location");
			me.description = infoObj.getString("introduction");
			me.homepage = infoObj.getString("homepage");
			me.head = infoObj.getString("head");
			me.gender = infoObj.getInt("sex");
			me.fansnum = infoObj.getInt("fansnum");
			me.idolnum = infoObj.getInt("idolnum");
			me.favnum = infoObj.getInt("favnum");
			me.statusnum = infoObj.getInt("tweetnum");
			me.regTime = infoObj.getString("regtime");
			if(!openid.equals("0")){
				if(infoObj.getInt("ismyfans") ==1) me.ismyfan = true;
				if(infoObj.getInt("ismyidol") == 1)me.ismyidol = true;
			}
			me.platform = User.PLATFORM_TENCENT_CODE;
		}
		uapi.shutdownConnection();
		task.result.add(me);
		Log.v(TAG, "get User Info:"+response);
		//Log.v(TAG, "get User object:"+me.toString());
	}

	
	
	/**
	 * 得到用户自己的粉丝列表和关注列表
	 * 处理结果为一个由User对象构成的ArrayList
	 * @param type 1为粉丝 2为关注
	 * @author sheling
	 * @throws Exception */
	public void getUserList(int type) throws Exception{
		FriendsAPI fapi = new FriendsAPI();
		Map<String, Object> map = task.param;
		String response = null;
		JSONObject obj = null;
		JSONArray rawArrays = null;
		ArrayList<User> users = new ArrayList<User>();
		switch(type){
		case 1:
			response = fapi.fanslist((OAuth)map.get("oauth"),"json",map.get("reqnum").toString(), 
					map.get("startindex").toString(),"0", "0");
			break;
		case 2:
			response = fapi.idollist((OAuth)map.get("oauth"), "json", map.get("reqnum").toString(), 
					map.get("startindex").toString(), "0");
			break;
		}
		
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		if(error.ret == 0){
			rawArrays = json.getJSONObject("data").getJSONArray("info");
			int length = rawArrays.length();
			for(int i=0;i<length;i++){
				obj = rawArrays.getJSONObject(i);
				User user = new User();
				user.id = obj.getString("openid");
				user.name=obj.getString("name");
				user.nick = obj.getString("nick");
				String province = obj.getString("province_code");
				if(province!=null && !province.equals("")) user.province = Integer.valueOf(province);
				String city = obj.getString("city_code");
				if(city !=null && !city.equals("")) user.city = Integer.valueOf(city);
				user.location = obj.getString("location");
//				user.description = obj.getString("");
//				user.homepage = obj.getString("homepage");
				user.head = obj.getString("head");
				user.gender = obj.getInt("sex");
				user.fansnum = obj.getInt("fansnum");
				user.idolnum = obj.getInt("idolnum");
//				user.favnum = obj.getInt("favnum");
//				user.statusnum = obj.getInt("tweetnum");
//				user.regTime = obj.getString("regtiuser");
				user.ismyfan = obj.getBoolean("isfans");
				user.ismyidol = obj.getBoolean("isidol");
				user.platform = User.PLATFORM_TENCENT_CODE;
				users.add(user);
			}
		}
		task.result.add(users);
		fapi.shutdownConnection();
		Log.v(TAG, "getUserList:"+type+" "+response);
		Log.v(TAG, "get object:"+users.toString());
	}
	
	/**
	 *  获取其他用户的粉丝列表，通过其他用户的用户名name
	 *  Map参数:reqnum:每次请求条数,startindex:起始位置（第一页填0，继续向下翻页：填【reqnum*（page-1）】,name:用户名
	 *  其中处理结果为一个ICArrayList
	 *  hasnext:0:为还可拉取，1：拉取完毕
	 *  list中存放粉丝的SimpleUser类的对象
	 *  @author allenjin
	 * @throws Exception 
	 */
	public void getOtherFanList() throws Exception{
		FriendsAPI fapi=new FriendsAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=fapi.userFanslist((OAuth)map.get("oauth"),"json", map.get("reqnum").toString(), 
				map.get("startindex").toString(),map.get("name").toString(),null,"0", "0");
		
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		
		Log.v(TAG,response);
		if(error.ret == 0){
			JSONObject data=new JSONObject(response).getJSONObject("data");
			ICArrayList ica=new ICArrayList();
			ica.hasNext=data.getInt("hasnext");
			if(data.get("info") instanceof JSONArray){
				JSONArray infolist=data.getJSONArray("info");
				for(int i=0;i<infolist.length();i++){
					JSONObject job=infolist.getJSONObject(i);
					User user=new User();
					user.head=job.getString("head");
					user.id=job.getString("openid");
					user.name=job.getString("name");
					user.nick=job.getString("nick");
					user.location=job.getString("location");
					user.platform=User.PLATFORM_TENCENT_CODE;
					user.fansnum=job.getInt("fansnum");
					ica.list.add(user);
					Log.v("第"+i+"个粉丝：", user.toString());
				}
			}
			task.result.add(ica);
		}
		fapi.shutdownConnection();
	}
	
	/**
	 *  获取其他用户的关注列表
	 *   Map参数:reqnum:每次请求条数,startindex:起始位置（第一页填0，继续向下翻页：填【reqnum*（page-1）】,name:用户名
	 *  其中处理结果为一个ICArrayList
	 *  hasnext:0:为还可拉取，1：拉取完毕
	 *  list中存放粉丝的SimpleUser类的对象
	 *  @author allenjin
	 * @throws Exception 
	 */
	public void getOtherIdolList() throws Exception{
		FriendsAPI fapi=new FriendsAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=fapi.userIdollist((OAuth)map.get("oauth"),"json", map.get("reqnum").toString(), 
				map.get("startindex").toString(),map.get("name").toString(),null,"0");
		
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		if(error.ret == 0){
			Log.v(TAG,response);
			JSONObject data=new JSONObject(response).getJSONObject("data");
			ICArrayList ica=new ICArrayList();
			ica.hasNext=data.getInt("hasnext");
			if(data.get("info") instanceof JSONArray){
				JSONArray infolist=data.getJSONArray("info");
				for(int i=0;i<infolist.length();i++){
					JSONObject job=infolist.getJSONObject(i);
					User user=new User();
					user.head=job.getString("head");
					user.id=job.getString("openid");
					user.name=job.getString("name");
					user.nick=job.getString("nick");
					user.platform=User.PLATFORM_TENCENT_CODE;
					user.location=job.getString("location");
					user.fansnum=job.getInt("fansnum");
					ica.list.add(user);
					Log.v("第"+i+"个关注：", user.toString());
				}
			}
			task.result.add(ica);
		}
		fapi.shutdownConnection();
	}
	
	/**根据微博ID获取评论列表
	 * @see Map参数:flag:标识。0－转播列表 1－点评列表 2－点评与转播列表,rooid:原微博ID
	 * pageflag 分页标识（0：第一页，1：向下翻页，2：向上翻页）,
	 * pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
	 * reqnum 每次请求记录的条数（1-100条）
	 * twitterid 翻页用，第1-100条填0，继续向下翻页，填上一次请求返回的最后一条记录id
	 * 处理结果为一个ICArrayList对象，
	 * hasnext为是否还有可拉取的评论，0为有，1为无，
	 * list存获取的评论列表Comment类的对象列表。
	 * @author allenjin
	 * @throws Exception 
	 */
	public void getCommentList() throws Exception{
		TAPI tapi=new TAPI();
		Map<String, Object> map=task.param;
		String response=null;
		response=tapi.reList((OAuth)map.get("oauth"),"json",map.get("flag").toString(),map.get("rootid").toString(),
				map.get("pageflag").toString(), map.get("pagetime").toString(),
				map.get("reqnum").toString(), map.get("twitterid").toString());
		
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		
		if(error.ret == 0){
			ICArrayList ica=new ICArrayList();
			JSONObject jsonobject=new JSONObject(response);
			JSONObject data=jsonobject.getJSONObject("data");
			ica.hasNext = data.getInt("hasnext");
			if(data.get("info")instanceof JSONArray){
			JSONArray info=data.getJSONArray("info");	
			for(int i=0;i<info.length();i++){
				Comment comment=new Comment();
				JSONObject infoobject=info.getJSONObject(i);
				comment.id=infoobject.getString("id");
				comment.openid = infoobject.getString("openid");
				comment.nick=infoobject.getString("nick");
				comment.text=infoobject.getString("origtext");
				comment.name = infoobject.getString("name");
				comment.timestamp=Integer.parseInt(infoobject.getString("timestamp"));
				ica.list.add(comment);
			  }	
			}
			task.result.add(ica);
		}
		tapi.shutdownConnection();
	}
	
	/**得到用户自己发表的微博列表
	 * Map参数：pageflag:分页标识，pagetime:本页起始时间，reqnum:每次请求条数,
	 * lastid:和pagetime配合使用，fopenids:需要读取的用户的openid列表,用下划线_隔开，(<=30);
	 * 处理结果为ICArrayList
	 * @author sheling
	 * @throws Exception */
	public void getUserWeiboList() throws Exception{
		StatusesAPI sapi = new StatusesAPI();
		Map<String, Object> map = task.param;
		String response = null;
		JSONObject jsonObj = null;
		JSONArray jsonArr = null,arrTmp = null;
		ICArrayList arraylist = new ICArrayList();
		response = sapi.broadcastTimeline((OAuth)map.get("oauth"), "json", map.get("pageflag").toString()
				, map.get("pagetime").toString(), map.get("reqnum").toString(), map.get("lastid").toString()
				, "0", "1");

		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		
		if(error.ret == 0){
			jsonObj = new JSONObject(response).getJSONObject("data");
			arraylist.hasNext = jsonObj.getInt("hasnext");
			if(jsonObj.get("info")instanceof JSONArray){
				jsonArr = jsonObj.getJSONArray("info");
				int length = jsonArr.length();
				for(int i=0;i<length;i++){
					jsonObj = jsonArr.getJSONObject(i);
					Status status = new Status();
					//TODO 封装为微博对象
					status.id = jsonObj.getString("id");
					status.text = jsonObj.getString("text");
					status.source = jsonObj.getString("from");
					if(jsonObj.get("image") instanceof JSONArray){
						arrTmp = jsonObj.getJSONArray("image");
						for(int j=0;j<arrTmp.length();j++){
							status.image.add(arrTmp.getString(j));	
						}
					}
					status.timestamp = jsonObj.getLong("timestamp");
					status.rCount = jsonObj.getInt("count");
					status.cCount = jsonObj.getInt("mcount");
					status.user.id = jsonObj.getString("openid");
					status.user.nick = jsonObj.getString("nick");
					status.user.head = jsonObj.getString("head");
					status.platform = User.PLATFORM_TENCENT_CODE;
					arraylist.list.add(status);
				}
			}
			task.result.add(arraylist);
		}
		sapi.shutdownConnection();
		Log.v(TAG, "getUserWeibos "+response);		
		Log.v(TAG, "getUserWeibos Obj"+arraylist);		
	}
	
	/**
	 * 获取用户提及时间线
	 * Map参数：pageflag:分页标识，pagetime:本页起始时间，reqnum:每次请求条数,lastid:和pagetime配合使用，
	 * type:0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评，若为0则为全部获取
	 * 处理结果为ICArrayList
	 * hasnext:0-表示还有微博可拉取，1-已拉取完毕
	 * list中存放发表的Status的微博对象列表
	 * @author allenjin
	 * @throws Exception 
	 */
	
	public void getMentionsWeiboList() throws Exception{
		StatusesAPI sapi=new StatusesAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=sapi.mentionsTimeline((OAuth)map.get("oauth"), "json", map.get("pageflag").toString(),
				map.get("pagetime").toString(), map.get("reqnum").toString(), 
				map.get("lastid").toString(), map.get("type").toString(), "0");

		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		
		Log.v(TAG, response);
		if(error.ret == 0){
		JSONObject data=new JSONObject(response).getJSONObject("data");
		ICArrayList ica=new ICArrayList();
		ica.hasNext=data.getInt("hasnext");
		if(data.get("info") instanceof JSONArray){
			JSONArray infolist=data.getJSONArray("info");
			for(int i=0;i<infolist.length();i++){
				JSONObject iobj=infolist.getJSONObject(i);
				Status s=new Status();
				s.id=iobj.getString("id");
				s.cCount=iobj.getInt("count");
				s.rCount=iobj.getInt("mcount");
				s.geo=null;
				s.platform=User.PLATFORM_TENCENT_CODE;
				s.source=iobj.getString("from");
				s.text=iobj.getString("origtext");
				s.timestamp=iobj.getLong("timestamp");
				s.user.head=iobj.getString("head");
				s.user.id=iobj.getString("openid");
				s.user.name=iobj.getString("name");
				s.user.nick=iobj.getString("nick");
				if(iobj.get("image")instanceof JSONArray){
					JSONArray imgs=iobj.getJSONArray("image");
					for(int j=0;j<imgs.length();j++){			
						s.image.add(imgs.getString(j));
					}
				}

				if(iobj.get("source") instanceof JSONObject){
					JSONObject res=iobj.getJSONObject("source");
					Status reStatus=new Status();
					reStatus.cCount=res.getInt("count");
					reStatus.rCount=res.getInt("mcount");
					reStatus.geo=null;
					reStatus.id=res.getString("id");
					reStatus.source=res.getString("from");
					reStatus.platform=User.PLATFORM_TENCENT_CODE;
					reStatus.timestamp=res.getLong("timestamp");
					reStatus.text=res.getString("origtext");
					reStatus.user.head=res.getString("head");
					reStatus.user.id=res.getString("openid");
					reStatus.user.name=res.getString("name");
					reStatus.user.nick=res.getString("nick");
					if(res.get("image")instanceof JSONArray){
						JSONArray image=res.getJSONArray("image");
						for(int j=0;j<image.length();j++){
							reStatus.image.add(image.getString(j));
						}
					}
					s.reStatus=reStatus;
				}
				ica.list.add(s);
				Log.v("第"+i+"个发表微博：",s.toString());
			}
			}
		task.result.add(ica);
		}
		sapi.shutdownConnection();
	}
	/**
	 * 获取主页的时间线
	 * @author allenjin
	 * @throws Exception
	 */
	public void getHomeTimeLine() throws Exception{
		StatusesAPI sapi=new StatusesAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=sapi.homeTimeline((OAuth)map.get("oauth"), "json", map.get("pageflag").toString(),
				map.get("pagetime").toString(), map.get("reqnum").toString(), "3", "0");
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		
		Log.v(TAG, response);
		if(error.ret == 0){
			JSONObject data=new JSONObject(response).getJSONObject("data");
			ICArrayList ica=new ICArrayList();
			ica.hasNext=data.getInt("hasnext");
			if(data.get("info") instanceof JSONArray){
				JSONArray infolist=data.getJSONArray("info");
				for(int i=0;i<infolist.length();i++){
					JSONObject iobj=infolist.getJSONObject(i);
					Status s=new Status();
					s.id=iobj.getString("id");
					s.cCount=iobj.getInt("count");
					s.rCount=iobj.getInt("mcount");
					s.geo=null;
					s.platform=User.PLATFORM_TENCENT_CODE;
					s.source=iobj.getString("from");
					s.text=iobj.getString("origtext");
					s.timestamp=iobj.getLong("timestamp");
					s.user.head=iobj.getString("head");
					s.user.id=iobj.getString("openid");
					s.user.name=iobj.getString("name");
					s.user.nick=iobj.getString("nick");
					if(iobj.get("image")instanceof JSONArray){
						JSONArray imgs=iobj.getJSONArray("image");
						for(int j=0;j<imgs.length();j++){			
							s.image.add(imgs.getString(j));
						}
					}

					if(iobj.get("source") instanceof JSONObject){
						JSONObject res=iobj.getJSONObject("source");
						Status reStatus=new Status();
						reStatus.cCount=res.getInt("count");
						reStatus.rCount=res.getInt("mcount");
						reStatus.geo=null;
						reStatus.id=res.getString("id");
						reStatus.source=res.getString("from");
						reStatus.platform=User.PLATFORM_TENCENT_CODE;
						reStatus.timestamp=res.getLong("timestamp");
						reStatus.text=res.getString("origtext");
						reStatus.user.head=res.getString("head");
						reStatus.user.id=res.getString("openid");
						reStatus.user.name=res.getString("name");
						reStatus.user.nick=res.getString("nick");
						if(res.get("image")instanceof JSONArray){
							JSONArray image=res.getJSONArray("image");
							for(int j=0;j<image.length();j++){
								reStatus.image.add(image.getString(j));
							}
						}
						s.reStatus=reStatus;
					}
					ica.list.add(s);
					Log.v("第"+i+"个发表微博：",s.toString());
				}
				}
			task.result.add(ica);
		}
		sapi.shutdownConnection();
	}
	/**
	 *获取其他用户发表的微博列表
	 *Map参数：pageflag:分页标识，pagetime:本页起始时间，reqnum:每次请求条数,
	 *	lastid:和pagetime配合使用，fopenids:需要读取的用户的openid列表,用下划线_隔开，(<=30);
	 *处理结果 为ICArrayList 通过组中的openid数组获取
	 *hasnext:0-表示还有微博可拉取，1-已拉取完毕
	 *list中存放发表的Status的微博对象列表
	 *@author allenjin
	 * @throws Exception 
	 */
	public void getOtherWeiboList() throws Exception{
		StatusesAPI sapi=new StatusesAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=sapi.usersTimeline((OAuth)map.get("oauth"), "json", map.get("pageflag").toString(),
				map.get("pagetime").toString(), map.get("reqnum").toString(), map.get("lastid").toString(),
				null, map.get("fopenids").toString(), "3","0");

		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		
		Log.v(TAG, response);
		if(error.ret == 0){
			JSONObject data=new JSONObject(response).getJSONObject("data");
			ICArrayList ica=new ICArrayList();
			ica.hasNext=data.getInt("hasnext");
			if(data.get("info") instanceof JSONArray){
				JSONArray infolist=data.getJSONArray("info");
				for(int i=0;i<infolist.length();i++){
					JSONObject iobj=infolist.getJSONObject(i);
					Status s=new Status();
					s.id=iobj.getString("id");
					s.cCount=iobj.getInt("count");
					s.rCount=iobj.getInt("mcount");
					s.geo=null;
					s.platform=User.PLATFORM_TENCENT_CODE;
					s.source=iobj.getString("from");
					s.text=iobj.getString("origtext");
					s.timestamp=iobj.getLong("timestamp");
					s.user.head=iobj.getString("head");
					s.user.id=iobj.getString("openid");
					s.user.name=iobj.getString("name");
					s.user.nick=iobj.getString("nick");
					if(iobj.get("image")instanceof JSONArray){
						JSONArray imgs=iobj.getJSONArray("image");
						for(int j=0;j<imgs.length();j++){			
							s.image.add(imgs.getString(j));
						}
					}

					if(iobj.get("source") instanceof JSONObject){
						JSONObject res=iobj.getJSONObject("source");
						Status reStatus=new Status();
						reStatus.cCount=res.getInt("count");
						reStatus.rCount=res.getInt("mcount");
						reStatus.geo=null;
						reStatus.id=res.getString("id");
						reStatus.source=res.getString("from");
						reStatus.platform=User.PLATFORM_TENCENT_CODE;
						reStatus.timestamp=res.getLong("timestamp");
						reStatus.text=res.getString("origtext");
						reStatus.user.head=res.getString("head");
						reStatus.user.id=res.getString("openid");
						reStatus.user.name=res.getString("name");
						reStatus.user.nick=res.getString("nick");
						if(res.get("image")instanceof JSONArray){
							JSONArray image=res.getJSONArray("image");
							for(int j=0;j<image.length();j++){
								reStatus.image.add(image.getString(j));
							}
						}
						s.reStatus=reStatus;
					}
					ica.list.add(s);
					Log.v("第"+i+"个发表微博：",s.toString());
				}
				}
			task.result.add(ica);
		}
		sapi.shutdownConnection();
	}
	
	/**
	 * 获取用户私信收件箱
	 * Map参数：pageflag:分页标识0，pagetime:本页起始时间0，reqnum:每次请求条数10,lastid:和pagetime配合使用0，
	 * 处理结果为ICArrayList
	 * 其中hastnext表示为：0-表示还有私信可拉取，1-已拉取完毕
	 * 其中list存放SimpleUser类的对象列表.
	 * @author allenjin
	 * @throws Exception 
	 */
	public void getPrivateMsgList() throws Exception{
		PrivateAPI papi=new PrivateAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=papi.recv((OAuth)map.get("oauth"), "json", map.get("pageflag").toString(),
				map.get("pagetime").toString(), map.get("reqnum").toString(),
				map.get("lastid").toString(), "0");

		if(response == null) throw new ConnectTimeoutException();
		Log.v(TAG, response);
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		if(error.ret == 0){
			JSONObject data=new JSONObject(response).getJSONObject("data");
			ICArrayList ica=new ICArrayList();
			if(data.get("info") instanceof JSONArray){
				JSONArray infolist=data.getJSONArray("info");
				for(int i=0;i<infolist.length();i++){
					JSONObject iobj=infolist.getJSONObject(i);
					Status s=new Status();
					s.id=iobj.getString("id");
					s.cCount=iobj.getInt("count");
					s.rCount=iobj.getInt("mcount");
					s.geo=null;
					s.platform=User.PLATFORM_TENCENT_CODE;
					s.source=iobj.getString("from");
					s.text=iobj.getString("origtext");
					s.timestamp=iobj.getLong("timestamp");
					s.user.head=iobj.getString("head");
					s.user.id=iobj.getString("openid");
					s.user.name=iobj.getString("name");
					s.user.nick=iobj.getString("nick");
					if(iobj.get("image")instanceof JSONArray){
						JSONArray imgs=iobj.getJSONArray("image");
						for(int j=0;j<imgs.length();j++){			
							s.image.add(imgs.getString(j));
						}
					}

					if(iobj.get("source") instanceof JSONObject){
						JSONObject res=iobj.getJSONObject("source");
						Status reStatus=new Status();
						reStatus.cCount=res.getInt("count");
						reStatus.rCount=res.getInt("mcount");
						reStatus.geo=null;
						reStatus.id=res.getString("id");
						reStatus.source=res.getString("from");
						reStatus.platform=User.PLATFORM_TENCENT_CODE;
						reStatus.timestamp=res.getLong("timestamp");
						reStatus.text=res.getString("origtext");
						reStatus.user.head=res.getString("head");
						reStatus.user.id=res.getString("openid");
						reStatus.user.name=res.getString("name");
						reStatus.user.nick=res.getString("nick");
						if(res.get("image")instanceof JSONArray){
							JSONArray image=res.getJSONArray("image");
							for(int j=0;j<image.length();j++){
								reStatus.image.add(image.getString(j));
							}
						}
						s.reStatus=reStatus;
					}
					ica.list.add(s);
					Log.v("第"+i+"个发表微博：",s.toString());
				}
				}
			task.result.add(ica);
		}
		papi.shutdownConnection();
	}
	
	/**
	 * 搜索用户列表，根据关键字
	 * Map参数： keyword:关键字匹配用户名和昵称,pagesize:记录的条数，page：从1开始
	 * 处理结果为ICArrayList
	 * 其中hastnext表示为：0，第一页且只有一页,1.多页第一页,还可向下翻页，2-还可向上翻页，3-可向上或向下翻页
	 * 其中list存放User类的对象列表.
	 * @author allenjin
	 * @throws Exception 
	 */
	public void searchUser() throws Exception{
		SearchAPI sapi=new SearchAPI();
		Map<String, Object> map = task.param;
		String response = null;
		response=sapi.user((OAuth)map.get("oauth"),map.get("keyword").toString(),
				map.get("pagesize").toString(), map.get("page").toString());

		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		if(error.ret == 0){
			JSONObject data=new JSONObject(response).getJSONObject("data");
			ICArrayList list=new ICArrayList();
			list.hasNext=data.getInt("hasnext");
			if(data.get("info")instanceof JSONArray){
				JSONArray info=data.getJSONArray("info");
				for(int j=0;j<info.length();j++){
					User users=new User();
					JSONObject iobj=info.getJSONObject(j);
					users.head=iobj.getString("head");
					users.id=iobj.getString("openid");
					users.name=iobj.getString("name");
					users.nick=iobj.getString("nick");
					users.location=iobj.getString("location");
					users.fansnum=iobj.getInt("fansnum");
					users.idolnum=iobj.getInt("idolnum");
					users.ismyidol=iobj.getBoolean("isidol");
					users.platform=User.PLATFORM_TENCENT_CODE;
					Log.v("第"+j+"个：用户", users.toString());
					list.list.add(users);
				}
			}
			Log.v(TAG,response);
			task.result.add(list);
		}
		sapi.shutdownConnection();
	}
	
	/**
	 * 收藏一个微博
	 *  Map参数:id:微博的id
	 * @author allenjin
	 * @throws Exception 
	 */
	public void addFavWeibo() throws Exception{
		FavAPI fav=new FavAPI();
		Map<String,Object> map=task.param;
		String response=null;
			response=fav.addFav((OAuth)map.get("oauth"), map.get("id").toString());

			if(response == null) throw new ConnectTimeoutException();
			Log.v(TAG, response);
			JSONObject json = new JSONObject(response);
			error.ret = json.getInt("ret");
			error.errorCode = json.getInt("errcode");
			error.detail = json.getString("msg");
		task.result.add(response);
		fav.shutdownConnection();
	}
	
	/**
	 * 取消一个收藏微博
	 * Map参数:id:微博的id
	 * @author allenjin
	 * @throws Exception 
	 */
	public void delFavWeibo() throws Exception{
		FavAPI fav=new FavAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=fav.delFav((OAuth)map.get("oauth"), map.get("id").toString());
		if(response == null) throw new ConnectTimeoutException();
		Log.v(TAG, response);
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		task.result.add(response);

		fav.shutdownConnection();
	}
	
	/**
	 * 获取收藏微博的列表
	 *Map参数：pageflag:分页标识0，pagetime:本页起始时间0，reqnum:每次请求条数10,lastid:和pagetime配合使用0，
	 * 处理结果为ICArrayList 
	 * 其中hasnext :0-表示还有微博可拉取，1-已拉取完毕
	 * 其中list存放收藏的微博的列表
	 * @author allenjin
	 * @throws Exception 
	 */
	public void getFavWeiboList() throws Exception{
		FavAPI fav=new FavAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=fav.listFav((OAuth)map.get("oauth"), map.get("pageflag").toString(), 
				map.get("pagetime").toString(),map.get("reqnum").toString(), map.get("lastid").toString());

		if(response == null) throw new ConnectTimeoutException();
			JSONObject json = new JSONObject(response);
			error.ret = json.getInt("ret");
			error.errorCode = json.getInt("errcode");
			error.detail = json.getString("msg");
			if(error.ret == 0){
			Log.v(TAG, response);
			ICArrayList iclist=new ICArrayList();
			JSONObject data=new JSONObject(response).getJSONObject("data");
			iclist.hasNext=data.getInt("hasnext");
			if(data.get("info")instanceof JSONArray){
				JSONArray info=data.getJSONArray("info");
				for(int i=0;i<info.length();i++){
					JSONObject iobj=info.getJSONObject(i);
					Status s=new Status();
					s.id=iobj.getString("id");
					s.cCount=iobj.getInt("count");
					s.rCount=iobj.getInt("mcount");
					s.geo=null;
					s.platform=User.PLATFORM_TENCENT_CODE;
					s.source=iobj.getString("from");
					s.text=iobj.getString("origtext");
					s.timestamp=iobj.getLong("timestamp");
					s.user.head=iobj.getString("head");
					s.user.id=iobj.getString("openid");
					s.user.name=iobj.getString("name");
					s.user.nick=iobj.getString("nick");
					if(iobj.get("image")instanceof JSONArray){
						JSONArray imgs=iobj.getJSONArray("image");
						for(int j=0;j<imgs.length();j++){			
							s.image.add(imgs.getString(j));
						}
					}

					if(iobj.get("source") instanceof JSONObject){
						JSONObject res=iobj.getJSONObject("source");
						Status reStatus=new Status();
						reStatus.cCount=res.getInt("count");
						reStatus.rCount=res.getInt("mcount");
						reStatus.geo=null;
						reStatus.id=res.getString("id");
						reStatus.source=res.getString("from");
						reStatus.platform=User.PLATFORM_TENCENT_CODE;
						reStatus.timestamp=res.getLong("timestamp");
						reStatus.text=res.getString("origtext");
						reStatus.user.head=res.getString("head");
						reStatus.user.id=res.getString("openid");
						reStatus.user.name=res.getString("name");
						reStatus.user.nick=res.getString("nick");
						if(res.get("image")instanceof JSONArray){
							JSONArray image=res.getJSONArray("image");
							for(int j=0;j<image.length();j++){
								reStatus.image.add(image.getString(j));
							}
						}
						s.reStatus=reStatus;
					}
					iclist.list.add(s);
					Log.v("第"+i+"个收藏微博：",s.toString());
				}
				task.result.add(iclist);
			}
		}
		fav.shutdownConnection();
	}
	
	
	
	/**
	 * 关注某人,通过用户名
	 * Map参数：name:用户名
	 * @author allenjin
	 * @throws Exception 
	 */
	public void addFriend() throws Exception{
		FriendsAPI fapi=new FriendsAPI();
		Map<String,Object> map=task.param;
		String response=null;
		response=fapi.add((OAuth)map.get("oauth"), "json", map.get("name").toString(), null);

		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		Log.v(TAG,response);
		task.result.add(response);
		fapi.shutdownConnection();
	}
	
	/**
	 * 取消关注某人，通过用户名
	 * Map参数：name:用户名
	 * @author allenjin
	 * @throws Exception 
	 * 
	 */
	public void delFriend() throws Exception{
		FriendsAPI fapi=new FriendsAPI();
		Map<String, Object> map=task.param;
		String response=null;
		response=fapi.del((OAuth)map.get("oauth"), "json", map.get("name").toString(), null);

		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		Log.v(TAG, response);
		task.result.add(response);
		fapi.shutdownConnection();
	}
	
	/**获取数据更新条数
	 * @throws Exception */
	public void getUpdateMsg() throws Exception{
		InfoAPI iapi = new InfoAPI();
		Map<String ,Object> map = task.param;
		String response = null;
		response = iapi.update((OAuth)map.get("oauth"), "json", map.get("op").toString(), map.get("type").toString());
		JSONObject data=new JSONObject(response).getJSONObject("data");
		if(response == null) throw new ConnectTimeoutException();
		JSONObject json = new JSONObject(response);
		error.ret = json.getInt("ret");
		error.errorCode = json.getInt("errcode");
		error.detail = json.getString("msg");
		if(error.ret == 0){
			HashMap<String,Integer> m = new HashMap<String,Integer>();
			m.put("home", data.getInt("home"));
			m.put("private", data.getInt("private"));
			m.put("fans", data.getInt("fans"));
			m.put("mentions", data.getInt("mentions"));
			task.result.add(m);
		}
		Log.d(TAG, response);
		iapi.shutdownConnection();
	}
}
