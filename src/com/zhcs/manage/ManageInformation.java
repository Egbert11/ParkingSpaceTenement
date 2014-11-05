package com.zhcs.manage;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.zhcs.community.CommunityInfo;
import com.zhcs.community.SpaceManageBean;
import com.zhcs.regAndlog.Login;
import com.zhcs.regAndlog.R;

public class ManageInformation extends Activity{
	private TextView userName, number, owner, state, dealCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_manage_information);
		userName = (TextView) findViewById(R.id.TextView04);
		number = (TextView) findViewById(R.id.textView6);
		owner = (TextView) findViewById(R.id.TextView01);
		state = (TextView) findViewById(R.id.TextView02);
		dealCount = (TextView) findViewById(R.id.TextView03);
		
		Bundle data = this.getIntent().getExtras();
		int index = data.getInt("index");
		ArrayList<SpaceManageBean> list = GetManageInfo.getList();
		final SpaceManageBean bean = list.get(index);
		
		userName.setText("�û�����"+CommunityInfo.getUserName());
		number.setText(String.valueOf(bean.getNumber()));
		owner.setText(String.valueOf(bean.getOwner()));
		switch(bean.getState()) {
		case 0:
			state.setText("���Ŷ�����");
			break;
		case 1:
			state.setText("����Ԥ����");
			break;
		case 2:
			state.setText("�Ѿ���Ԥ��");
			break;
		default:
			break;
		}
		dealCount.setText(String.valueOf(bean.getDealNum()));
	}
	
	public void logoutFun(View v) {
		Intent intent = new Intent(ManageInformation.this, Login.class);
		startActivity(intent);
		finish();
	}

}
