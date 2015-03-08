package com.zhcs.manage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.zhcs.community.CommunityInfo;
import com.zhcs.community.SpaceManageBean;
import com.zhcs.regAndlog.Login;
import com.zhcs.regAndlog.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Administrator
 * @func 车位状态
 */
public class ManageMap extends Activity{

	private ListView mListView;
	private static ArrayList<SpaceManageBean> list = GetManageInfo.getList();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_manage_map);
		
		mListView = (ListView)findViewById(R.id.listView1);
		
		//将返回列表按照车位号进行排序
		Comparator<SpaceManageBean> comparator = new Comparator<SpaceManageBean>(){
			   public int compare(SpaceManageBean s1, SpaceManageBean s2) {
			    if(s1.getNumber() != s2.getNumber()){
			     return s1.getNumber() - s2.getNumber();
			    } else {
			    	return s1.getDealNum() - s2.getDealNum();
			    }
			   }
		};
		Collections.sort(list,comparator);
		
		mListView.setAdapter(new SpaceListAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ManageMap.this, ManageInformation.class);
				Bundle bun = new Bundle();
				bun.putInt("index", position);
				intent.putExtras(bun);
				startActivity(intent);
			}
		});
	}
	
	public void logoutFun(View v) {
		Intent intent = new Intent(ManageMap.this, Login.class);
		startActivity(intent);
		finish();
	}
	
	private class SpaceListAdapter extends BaseAdapter{
		public SpaceListAdapter(){
			super();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(ManageMap.this, R.layout.manageinfo_item, null);
			TextView title = (TextView)convertView.findViewById(R.id.title);
			int num = position + 1;
			title.setText("车位" + num);
			return convertView;
		}
		
	}
}



