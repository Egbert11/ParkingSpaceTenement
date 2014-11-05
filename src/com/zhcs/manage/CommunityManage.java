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
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_registered);
		
		userName = (TextView)findViewById(R.id.textView1);
		
		userName.setText("�û�����"+CommunityInfo.getUserName());
	}
	
	//һ����֤
	public void checkFun(View v) {
		Intent intent=new Intent();
		intent.setClass(CommunityManage.this,CaptureActivity.class);
		startActivity(intent);
	}
	
	//��λ����
	public void manageFun(View v) {
		//��ȡ��λ����Ϣ
		GetManageInfo.getSpaceInfo(CommunityManage.this);
	}
	
	//��ʷ����
	public void historyFun(View v) {
		GetHistoryInfo.getSpaceInfo(CommunityManage.this);
		
	}
	
	//ע��
	public void logoutFun(View v) {
		Intent intent = new Intent(CommunityManage.this, Login.class);
		startActivity(intent);
		finish();
	}
	
	 @Override
	public void onBackPressed() {
	    //ʵ��Home��Ч������Ҫ���Ȩ��:<uses-permission android:name="android.permission.RESTART_PACKAGES" />
	    //super.onBackPressed();��仰һ��Ҫע��,��Ȼ��ȥ����Ĭ�ϵ�back����ʽ��
	    Intent i= new Intent(Intent.ACTION_MAIN);
	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    i.addCategory(Intent.CATEGORY_HOME);
	    startActivity(i); 
	}
}
