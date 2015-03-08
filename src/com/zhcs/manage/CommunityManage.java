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
 * @func ��ҵ��������
 */
public class CommunityManage extends Activity{
	private TextView userName;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_page);
	}
	
	
	//��λ����
	public void manageFun(View v) {
		//��ȡ��λ����Ϣ
		GetManageInfo.getSpaceInfo(CommunityManage.this);
	}
	
	//������֤
	public void enterFun(View v) {
		Intent intent=new Intent();
		intent.putExtra("type", "enter");
		intent.setClass(CommunityManage.this,CaptureActivity.class);
		startActivity(intent);
	}
	
	//�뿪��֤
	public void leaveFun(View v) {
		Intent intent=new Intent();
		intent.putExtra("type", "leave");
		intent.setClass(CommunityManage.this,CaptureActivity.class);
		startActivity(intent);
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
