package com.kernel.intelcurrent.activity;

import java.util.LinkedList;
import com.kernel.intelcurrent.adapter.TimelineListAdapter;
import com.kernel.intelcurrent.model.Group;
import com.kernel.intelcurrent.model.ICArrayList;
import com.kernel.intelcurrent.model.Status;
import com.kernel.intelcurrent.model.Task;
import com.kernel.intelcurrent.service.MainService;
import com.kernel.intelcurrent.widget.PullToRefreshListView;
import com.kernel.intelcurrent.widget.PullToRefreshListView.OnLoadMoreListener;
import com.kernel.intelcurrent.widget.PullToRefreshListView.OnRefreshListener;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;


public class TimelineActivity extends Activity implements Updateable{

	public static final int REQUEST_TYPE_INIT_TIMELINE = 1;
	public static final int REQUEST_TYPE_REFRESH = 2;
	public static final int REQUEST_TYPE_PAGE_DOWN = 3;
	
	private static final String TAG = TimelineActivity.class.getSimpleName();
	private PullToRefreshListView lv;
	private Group group;
	private LinkedList<Status> statuses = new LinkedList<Status>();
	private int hasNext = -1;
	private int state = -1;
	
	private MainActivity activityGroup;
	private MainService mService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityGroup = (MainActivity)getParent();
		mService = activityGroup.getService();
		setContentView(R.layout.activity_timeline);
		//第一次请求数据
		init();
		findViews();
		setListeners();
	}
	
	@Override
	public void update(int type, Object param) {
		//类型较检，不符合自己类型的数据忽略
		if(type != Task.G_GET_GROUP_TIMELINE)return;
		if(((Task)param).result.size() == 0) return;
		//根据之前标志位的状态决定刷新来做什么
		ICArrayList result;
		LinkedList<Status> tmpList;
		switch(state){
		//初始化
		case REQUEST_TYPE_INIT_TIMELINE:
			result = (ICArrayList)((Task)param).result.get(0);
			hasNext = result.hasNext;
			for(Object status: result.list){
				statuses.add((Status)status);
			}
			setAdapter();
			break;
		//刷新第一页
		case REQUEST_TYPE_REFRESH:
			tmpList = new LinkedList<Status>();
			result = (ICArrayList)((Task)param).result.get(0);
			hasNext = result.hasNext;
			for(Object status: result.list){
				//判断当前已有的最新一条记录与请求的刷新记录是否相同，相同则不读取之后的记录
				if(!statuses.get(0).id.equals(((Status)status).id))
						tmpList.add((Status)status);
				else break;
			}
			statuses.addAll(0, tmpList);
			lv.onRefreshComplete();
			Toast.makeText(this, tmpList.size() == 0? "没有最新消息":"刷新了"+tmpList.size()+"条微博", Toast.LENGTH_SHORT).show();
			break;
		//下翻页
		case REQUEST_TYPE_PAGE_DOWN:
			tmpList = new LinkedList<Status>();
			result = (ICArrayList)((Task)param).result.get(0);
			hasNext = result.hasNext;
			for(Object status: result.list){
				tmpList.add((Status)status);
			}
			statuses.addAll(statuses.size(), tmpList);
			lv.onLoadMoreComplete();
			Toast.makeText(this, "又拉取了一页",Toast.LENGTH_SHORT).show();
			break;
		}
		//清零标志位
		state  = -1;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			LinearLayout container=(LinearLayout)((ActivityGroup)getParent()).getWindow().findViewById(R.id.layout_main_layout_container);	
			container.removeAllViews();
	        Intent intent=new Intent(this,GroupBlockActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        Window subActivity=((ActivityGroup)this.getParent()).getLocalActivityManager().startActivity(GroupBlockActivity.class.getSimpleName(),intent);
	        container.addView(subActivity.getDecorView());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void init(){
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("ext");
		group = (Group) bundle.getSerializable("group");

		state = 1;
		mService.getTimeline(group, 0, 0, "0");
		Log.d(TAG, "myservice:"+mService);
	}
	private void findViews(){
		lv = (PullToRefreshListView)findViewById(R.id.activity_timeline_lv_main);
	}
	
	private void setListeners(){
		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				state = 2;
				mService.getTimeline(group, 0, 0, "0");
			}
		});
		lv.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				state = 3;
				mService.getTimeline(group, 1, statuses.getLast().timestamp, statuses.getLast().id);
			}
		});
	}
	
	private void setAdapter(){
		if(statuses.size() != 0)
			lv.setAdapter(new TimelineListAdapter(this, statuses));
	}

	
}