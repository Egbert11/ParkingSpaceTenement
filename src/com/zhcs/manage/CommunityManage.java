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

/**
 * 
 * @author Administrator
 * @func 物业端主界面
 */
public class CommunityManage extends Activity{
	private TextView userName;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_page);
	}
	
	
	//车位管理
	public void manageFun(View v) {
		//获取车位的信息
		GetManageInfo.getSpaceInfo(CommunityManage.this);
	}
	
	//进入验证
	public void enterFun(View v) {
		Intent intent=new Intent();
		intent.putExtra("type", "enter");
		intent.setClass(CommunityManage.this,CaptureActivity.class);
		startActivity(intent);
	}
	
	//离开验证
	public void leaveFun(View v) {
		Intent intent=new Intent();
		intent.putExtra("type", "leave");
		intent.setClass(CommunityManage.this,CaptureActivity.class);
		startActivity(intent);
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
