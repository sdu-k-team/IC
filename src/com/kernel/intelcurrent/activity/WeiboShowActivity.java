package com.kernel.intelcurrent.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.kernel.intelcurrent.adapter.CommentListAdapter;
import com.kernel.intelcurrent.model.Comment;
import com.kernel.intelcurrent.model.ICArrayList;
import com.kernel.intelcurrent.model.SimpleUser;
import com.kernel.intelcurrent.model.Status;
import com.kernel.intelcurrent.model.Task;
import com.kernel.intelcurrent.model.User;
import com.kernel.intelcurrent.widget.UrlImageView;
import com.kernel.intelcurrent.widget.WeiboTextView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeiboShowActivity extends BaseActivity implements View.OnClickListener,Updateable{

	private static final String TAG = WeiboShowActivity.class.getSimpleName();
	
	private static final int REQUEST_FIRST = 1;
	private static final int REQUEST_LOAD_MORE = 2;
	
	private ImageView leftImage;
	private TextView titleTv,platformTv,nameTv,retweetNameTv,commentBtn,forwordBtn,moreBtn,retweetMargin,loadMoreTv;
	private UrlImageView headIv,picIv,retweetPicIv;
	private WeiboTextView contentIv,retweetContentIv;
	private RelativeLayout retweetLayout;
	private ListView commentsLv;
	
	private Status status;
	private SimpleUser user;
	
	private int hasNext;
	private LinkedList<Comment> comments = new LinkedList<Comment>();
	private CommentListAdapter listAdapter;
	private LayoutInflater mInflater;
	private View headView,footView;//listview上方的头视图
	private int request = -1;
	
	@Override
	public void update(int type, Object param) {
		Log.d(TAG, "task"+param);
		if(type != Task.WEIBO_COMMENTS_BY_ID)return;
		if(((Task)param).result.size() == 0) return;
		ICArrayList result =(ICArrayList) ((Task)param).result.get(0);
		if(request == REQUEST_FIRST){
			for(Object comment: result.list){
				comments.add((Comment)comment);
			}
			setAdapter();
		}else if(request == REQUEST_LOAD_MORE){
			ArrayList<Comment> news = new ArrayList<Comment>();
			for(Object comment: result.list){
				news.add((Comment)comment);
			}
			comments.addAll(comments.size(),news);
			listAdapter.notifyDataSetChanged();
			loadMoreTv.setText(getResources().getString(R.string.common_load_more));
		}
		request = -1;
		Log.d(TAG, "comments"+comments);
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo_show);
		Intent intent = getIntent();
		status = (Status) intent.getSerializableExtra("status");
		user = status.user;
		findViews();
		init();
		setListeners();
		setAdapter();
	}
	
	private void findViews(){
		leftImage = (ImageView)findViewById(R.id.common_head_iv_left);
		titleTv = (TextView)findViewById(R.id.common_head_tv_title);
		headIv = (UrlImageView)findViewById(R.id.activity_weibo_show_iv_head);
		nameTv = (TextView)findViewById(R.id.activity_weibo_show_tv_name);
		platformTv = (TextView)findViewById(R.id.activity_weibo_show_tv_platform);
		commentBtn = (TextView)findViewById(R.id.activity_weibo_show_tv_btn_1);
		forwordBtn = (TextView)findViewById(R.id.activity_weibo_show_tv_btn_2);
		moreBtn = (TextView)findViewById(R.id.activity_weibo_show_tv_btn_3);
		
		mInflater = LayoutInflater.from(this);
		headView = mInflater.inflate(R.layout.activity_weibo_show_head, null);
		footView = mInflater.inflate(R.layout.common_foot_load_more, null);
		
		contentIv = (WeiboTextView)headView.findViewById(R.id.activity_weibo_show_tv_tweet_txt);
		picIv = (UrlImageView)headView.findViewById(R.id.activity_weibo_show_urlimage_tweet_image);
		retweetLayout = (RelativeLayout)headView.findViewById(R.id.activity_weibo_show_layout_right_retweet_content);
		retweetContentIv = (WeiboTextView)headView.findViewById(R.id.activity_weibo_show_tv_retweet_txt);
		retweetPicIv = (UrlImageView)headView.findViewById(R.id.activity_weibo_show_urlimage_retweet_image);
		retweetNameTv = (TextView)headView.findViewById(R.id.activity_weibo_show_tv_retweet_head);
		
		retweetMargin = (TextView)headView.findViewById(R.id.activity_weibo_show_tv_retweet_margin);
		commentsLv = (ListView)findViewById(R.id.activity_weibo_show_lv_comments);
		commentsLv.addHeaderView(headView);
		
		loadMoreTv = (TextView)footView.findViewById(R.id.common_foot_load_more_tv);
		commentsLv.addFooterView(loadMoreTv,null,true);
		
		leftImage.setImageResource(R.drawable.ic_title_back);
		titleTv.setText(R.string.weibo_show_title);
	}
	
	private void init(){
		try {
			headIv.bindUrl(user.head,UrlImageView.PLAT_FORM_TENCENT, UrlImageView.LARGE_HEAD);
			if(status.image != null && status.image.size() > 0){
				picIv.bindUrl(status.image.get(0), UrlImageView.PLAT_FORM_TENCENT, UrlImageView.SMALL_IMAGE);
				picIv.setVisibility(View.VISIBLE);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		nameTv.setText(user.nick);
		platformTv.setText(user.platform == User.PLATFORM_SINA_CODE ? User.PLATFORM_SINA:User.PLATFORM_TENCENT);
		
		contentIv.setText(status.text,true);
		if(status.reStatus!= null){
			retweetLayout.setVisibility(View.VISIBLE);
			retweetContentIv.setText(status.reStatus.text,true);
			retweetNameTv.setText(status.reStatus.user.nick);
			retweetMargin.setVisibility(View.VISIBLE);
			if(status.reStatus.image != null&& status.reStatus.image.size() > 0){
				try {
					retweetPicIv.bindUrl(status.reStatus.image.get(0), UrlImageView.PLAT_FORM_TENCENT, UrlImageView.SMALL_IMAGE);
					retweetPicIv.setVisibility(View.VISIBLE);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setListeners(){
		leftImage.setOnClickListener(this);
		commentBtn.setOnClickListener(this);
		forwordBtn.setOnClickListener(this);
		moreBtn.setOnClickListener(this);
		loadMoreTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == commentBtn){
			Intent intent = new Intent(this,WeiboNewActivity.class);
			intent.putExtra("model", WeiboNewActivity.MODEL_NEW_COMMENT);
			intent.putExtra("platform", Task.PLATFORM_TENCENT);
			intent.putExtra("ext", status.id);
			startActivity(intent);
		}else if(v == forwordBtn){
			Intent intent = new Intent(this,WeiboNewActivity.class);
			intent.putExtra("model", WeiboNewActivity.MODEL_FORWORD);
			intent.putExtra("platform", Task.PLATFORM_TENCENT);
			intent.putExtra("ext", status.id);
			startActivity(intent);
		}else if(v == moreBtn){
			
		}else if(v == loadMoreTv){
			//当前无其他请求再去加载更多
			if(request == -1){
				request = REQUEST_LOAD_MORE;
				Comment lastComment = comments.get(comments.size() -1);
				mService.getCommentList(status.id, 1, 
						lastComment.timestamp,comments.size() > 100
						? lastComment.id : "0", Task.PLATFORM_TENCENT);
				loadMoreTv.setText(getResources().getString(R.string.common_loading));
			}
		}else if(v == leftImage){
			finish();
		}
	}

	private void setAdapter(){
		listAdapter = new CommentListAdapter(this, comments);
		commentsLv.setAdapter(listAdapter);
	}
	
	@Override
	public void onConnectionFinished() {
//		if(user.platform == User.PLATFORM_TENCENT_CODE){
			request = REQUEST_FIRST;
			mService.getCommentList(status.id,0,0,"0",Task.PLATFORM_TENCENT);	
//		}
	}

	@Override
	public void onConnectionDisConnected() {
		
	}



}
