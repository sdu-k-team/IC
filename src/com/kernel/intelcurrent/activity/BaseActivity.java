package com.kernel.intelcurrent.activity;
import com.kernel.intelcurrent.*;
import com.kernel.intelcurrent.service.MainService;
import com.kernel.intelcurrent.service.MainService.ICBinder;

import android.app.*;
import android.content.*;
import android.os.*;
/**
 * classname:BaseActivity.java
 * @author ������
 * */
public abstract class BaseActivity extends Activity
{
    private static final String TAG=BaseActivity.class.getSimpleName();
    private MainService mService;
    private ServiceConnection connection;
    boolean isBound=false;
    
    /**
     * ��������̳�update��������service����activity
     * 
     * */
    public abstract  void update(int type ,Object param);

    
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
 	    setContentView(R.layout.activity_base);
    }
    protected void onStart()
   	{
   	   super.onStart();
   	   Intent intent=new Intent(this,MainService.class);
   	   bindService(intent,connection, Context.BIND_AUTO_CREATE);
   	}
    protected void onStop()
	{
	   super.onStop();
	   unbindService(connection);
	   isBound=false;
	}
   
    /**
     * inner class:ICConnection
     * */
    private class ICConnection implements ServiceConnection
    {

		public void onServiceConnected(ComponentName className, IBinder service)
		{
			ICBinder binder=(ICBinder)service;
			mService=binder.getService();
			isBound=true;
			mService.changeCurrentActivity(BaseActivity.this);			
		}

		public void onServiceDisconnected(ComponentName name) 
		{
			isBound = false;
		}
    	
    }
}
