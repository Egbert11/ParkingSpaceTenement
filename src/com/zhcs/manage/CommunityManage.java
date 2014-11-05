package com.zhcs.manage;

import zxing.standopen.ZxingCreate;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.zhcs.community.CommunityInfo;
import com.zhcs.regAndlog.Login;
import com.zhcs.regAndlog.R;
import com.zijunlin.Zxing.Demo.CaptureActivity;

public class CommunityManage extends Activity{
	private TextView userName;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_registered);
		
		userName = (TextView)findViewById(R.id.textView1);
		
		userName.setText("用户名："+CommunityInfo.getUserName());
	}
	
	//一键验证
	public void checkFun(View v) {
		Intent intent=new Intent();
		intent.setClass(CommunityManage.this,CaptureActivity.class);
		startActivity(intent);
	}
	
	//车位管理
	public void manageFun(View v) {
		//获取车位的信息
		GetManageInfo.getSpaceInfo(CommunityManage.this);
	}
	
	//历史订单
	public void historyFun(View v) {
		GetHistoryInfo.getSpaceInfo(CommunityManage.this);
		
	}
	
	//注销
	public void logoutFun(View v) {
		Intent intent = new Intent(CommunityManage.this, Login.class);
		startActivity(intent);
		finish();
	}
	
	 @Override
	public void onBackPressed() {
	    //实现Home键效果，需要添加权限:<uses-permission android:name="android.permission.RESTART_PACKAGES" />
	    //super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了
	    Intent i= new Intent(Intent.ACTION_MAIN);
	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    i.addCategory(Intent.CATEGORY_HOME);
	    startActivity(i); 
	}
}
