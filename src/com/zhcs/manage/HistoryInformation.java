package com.zhcs.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.zhcs.community.CommunityInfo;
import com.zhcs.community.SpaceHistoryBean;
import com.zhcs.regAndlog.Login;
import com.zhcs.regAndlog.R;

public class HistoryInformation extends Activity{
	private TextView userName, number, owner, time, cost, fine, dealCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_history_information);
		userName = (TextView) findViewById(R.id.textView1);
		number = (TextView) findViewById(R.id.textView6);
		owner = (TextView) findViewById(R.id.TextView03);
		time = (TextView) findViewById(R.id.TextView04);
		cost = (TextView) findViewById(R.id.TextView05);
		fine = (TextView) findViewById(R.id.TextView06);
		dealCount = (TextView) findViewById(R.id.TextView07);
		
		Bundle data = this.getIntent().getExtras();
		int index = data.getInt("index");
		ArrayList<SpaceHistoryBean> list = GetHistoryInfo.getList();
		final SpaceHistoryBean bean = list.get(index);
		
		userName.setText("用户名："+CommunityInfo.getUserName());
		number.setText(String.valueOf(bean.getNumber()));
		owner.setText(String.valueOf(bean.getOwner()));
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
		String day=dateformat.format(bean.getDate());
		//String day = ""+bean.getDate().getYear()+":"+bean.getDate().getMonth()+":"+bean.getDate().getDate();
		time.setText(day+" "+bean.getStart()+":00--"+bean.getEnd()+":00");
		cost.setText(String.valueOf(bean.getCost())+"元");
		fine.setText("无");
		dealCount.setText(String.valueOf(bean.getDealNum()));
	}
	
	public void logoutFun(View v) {
		Intent intent = new Intent(HistoryInformation.this, Login.class);
		startActivity(intent);
		finish();
	}

}
