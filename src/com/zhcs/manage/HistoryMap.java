package com.zhcs.manage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.zhcs.community.CommunityInfo;
import com.zhcs.community.SpaceHistoryBean;
import com.zhcs.regAndlog.Login;
import com.zhcs.regAndlog.R;

public class HistoryMap extends Activity{

	private ListView mListView;
	private TextView userName;
	private static ArrayList<SpaceHistoryBean> list = GetHistoryInfo.getList();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_history_list);
		
		userName = (TextView)findViewById(R.id.textView1);
		mListView = (ListView)findViewById(R.id.listView1);
		//将返回列表按照车位号进行排序
		Comparator<SpaceHistoryBean> comparator = new Comparator<SpaceHistoryBean>(){
			   public int compare(SpaceHistoryBean s1, SpaceHistoryBean s2) {
			    //先排车位号
			    if(s1.getNumber() != s2.getNumber()){
			     return s1.getNumber() - s2.getNumber();
			    } else {
			    	return s1.getDealNum() - s2.getDealNum();
			    }
			   }
		};
		Collections.sort(list,comparator);
		Log.e("size",String.valueOf(list.size()));
		userName.setText("用户名:"+CommunityInfo.getUserName());
		mListView.setAdapter(new SpaceListAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HistoryMap.this, HistoryInformation.class);
				Bundle bun = new Bundle();
				bun.putInt("index", position);
				intent.putExtras(bun);
				startActivity(intent);
			}
		});
	}
	
	public void logoutFun(View v) {
		Intent intent = new Intent(HistoryMap.this, Login.class);
		startActivity(intent);
		finish();
	}
	
	private class SpaceListAdapter extends BaseAdapter{
		public SpaceListAdapter(){
			super();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			convertView = View.inflate(HistoryMap.this, R.layout.historyinfo_item, null);
			TextView number = (TextView)convertView.findViewById(R.id.number);
			TextView name = (TextView)convertView.findViewById(R.id.name);
			TextView count = (TextView)convertView.findViewById(R.id.count);
			Log.e("historyMap","日期："+list.get(position).getDate());
			number.setText("车位" + list.get(position).getNumber());
			name.setText(list.get(position).getOwner());
			count.setText(String.valueOf(list.get(position).getDealNum()));
			return convertView;
//			convertView = View.inflate(HistoryMap.this, R.layout.manageinfo_item, null);
//			TextView title = (TextView)convertView.findViewById(R.id.title);
//			int num = position + 1;
//			title.setText("订单" + num);
//			return convertView;
		}
		
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			moveTaskToBack(true);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}



