package com.zhcs.manage;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.zhcs.community.CommunityInfo;
import com.zhcs.regAndlog.Login;
import com.zhcs.regAndlog.R;

public class EnterVerify extends Activity{
	private TextView address, number, start_time, end_time;
	private String objId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_enter_verify);
		
		address = (TextView)findViewById(R.id.community_name);
		number = (TextView)findViewById(R.id.space_number);
		start_time = (TextView)findViewById(R.id.start_time);
		end_time = (TextView)findViewById(R.id.end_time);
		
		Bundle bun = this.getIntent().getExtras();
		objId = bun.getString("info");
		resolutionData(objId);
	}
	
	//����ID����
	private void resolutionData(String id) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		query.whereEqualTo("objectId", id);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException e) {
				if (e == null) {
		            AVObject obj = arg0.get(0);
		            //��ȡС��id�Լ���ַ
		            String communityid = obj.getString("communityid");
		            AVQuery<AVObject> query = new AVQuery<AVObject>("Community");
		    		query.whereEqualTo("objectId", communityid);
		    		query.findInBackground(new FindCallback<AVObject>() {
		    			@Override
		    			public void done(List<AVObject> arg0, AVException e) {
		    				if (e == null) {
		    					AVObject com = arg0.get(0);
		    					String add = com.getString("address");
		    					address.setText(add);
		    				}else {
		    			        
		    		        }
		    			}
		    		});
		    		//��ȡ��ţ���ʼʱ��ͽ���ʱ��
		    		number.setText(obj.getNumber("number").toString());
		    		SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd ");
		    		SimpleDateFormat dateformat2=new SimpleDateFormat("HH:mm");
		    		String day = dateformat1.format(obj.getCreatedAt());
		    		String start=dateformat2.format(obj.getDate("start"));
		    		start_time.setText(day + start);
		    		String end=dateformat2.format(obj.getDate("end"));
		    		end_time.setText(day + end);
		        }
			}
		});
	}
	
	/**
	 * 
	 * @param v
	 * @function �������С��
	 */
	public void enterAllow(View v) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		Log.d("objectid", objId);
		
		query.whereEqualTo("objectId", objId);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            AVObject mod = avObjects.get(0);
		            //�޸�Ϊ����״̬
					mod.put("state", 2);
					mod.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								Toast.makeText(EnterVerify.this,"�ɹ����ó�������״̬", Toast.LENGTH_SHORT).show();
								//��ȡ�����ѷ�����λ��Ϣ
					        } else {
					        	Toast.makeText(EnterVerify.this,"�ϴ����ݳ���������", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }else {
		        	Toast.makeText(EnterVerify.this,"������������", Toast.LENGTH_SHORT).show();
		        }
			}
		});
	}
	
	/**
	 * 
	 * @param v
	 * @function ��ֹ����С��
	 */
	public void enterProhibit(View v) {
		Toast.makeText(EnterVerify.this,"�ɹ����ý�ֹ��������", Toast.LENGTH_SHORT).show();
	}
}
