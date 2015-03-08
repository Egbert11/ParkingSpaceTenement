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

/**
 * 
 * @author Administrator
 * @func ��λ״̬��Ϣ�б�
 */
public class HistoryMap extends Activity{

	private ListView mListView;
	private static ArrayList<SpaceHistoryBean> list = GetHistoryInfo.getList();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_history_list);
		
		mListView = (ListView)findViewById(R.id.listView1);
		//�������б��ճ�λ�Ž�������
		Comparator<SpaceHistoryBean> comparator = new Comparator<SpaceHistoryBean>(){
			   public int compare(SpaceHistoryBean s1, SpaceHistoryBean s2) {
			    //���ų�λ��
			    if(s1.getNumber() != s2.getNumber()){
			     return s1.getNumber() - s2.getNumber();
			    } else {
			    	return s1.getDealNum() - s2.getDealNum();
			    }
			   }
		};
		Collections.sort(list,comparator);
		Log.e("size",String.valueOf(list.size()));
		mListView.setAdapter(new SpaceListAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
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
			convertView = View.inflate(HistoryMap.this, R.layout.historyinfo_item, null);
			TextView number = (TextView)convertView.findViewById(R.id.number);
			TextView name = (TextView)convertView.findViewById(R.id.name);
			TextView count = (TextView)convertView.findViewById(R.id.count);
			Log.e("historyMap","���ڣ�"+list.get(position).getDate());
			number.setText("��λ" + list.get(position).getNumber());
			name.setText(list.get(position).getOwner());
			count.setText(String.valueOf(list.get(position).getDealNum()));
			return convertView;
//			convertView = View.inflate(HistoryMap.this, R.layout.manageinfo_item, null);
//			TextView title = (TextView)convertView.findViewById(R.id.title);
//			int num = position + 1;
//			title.setText("����" + num);
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



