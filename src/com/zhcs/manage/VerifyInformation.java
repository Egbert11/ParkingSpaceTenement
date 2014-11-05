package com.zhcs.manage;

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

public class VerifyInformation extends Activity{
	private TextView userName, number, address, time, pay;
	private String objId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_verify_information);
		
		userName = (TextView)findViewById(R.id.textView1);
		number = (TextView)findViewById(R.id.textView6);
		address = (TextView)findViewById(R.id.textView7);
		time = (TextView)findViewById(R.id.textView8);
		pay = (TextView)findViewById(R.id.textView9);
		
		userName.setText("用户名："+CommunityInfo.getUserName());
		Bundle bun = this.getIntent().getExtras();
		objId = bun.getString("info");
		Toast.makeText(VerifyInformation.this, objId, Toast.LENGTH_SHORT).show();
		
		resolutionData(objId);
	}
	//订单ID解析
	private void resolutionData(String id) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		query.whereEqualTo("objectId", id);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException e) {
				if (e == null) {
		            AVObject obj = arg0.get(0);
		            //获取小区id以及地址
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
		    		//获取编号，时间以及费用
		    		number.setText(obj.getNumber("number").toString());
		    		time.setText(obj.getNumber("start").toString()+":00--"+obj.getNumber("end").toString()+":00");
		    		pay.setText(obj.getNumber("cost").toString()+"元");
		        } else {
		        
		        }
			}
		});
	}
	
	//进入小区
	public void enterFun(View v) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		Log.d("objectid", objId);
		
		query.whereEqualTo("objectId", objId);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("修改成功", "查询到" + avObjects.size() + " 条符合条件的数据");
		            AVObject mod = avObjects.get(0);
		          //修改为进入状态
					mod.put("state", 2);
					mod.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								Toast.makeText(VerifyInformation.this,"允许车辆进入", Toast.LENGTH_SHORT).show();
								//获取最新已发布车位信息
					        } else {
					        	Toast.makeText(VerifyInformation.this,"上传数据出错，请重试", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }else {
		        	Toast.makeText(VerifyInformation.this,"不允许车辆进入", Toast.LENGTH_SHORT).show();
		        }
			}
		});
	}
	
	//离开小区
	public void leaveFun(View v) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		Log.d("objectid", objId);
		
		query.whereEqualTo("objectId", objId);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("修改成功", "查询到" + avObjects.size() + " 条符合条件的数据");
		            final AVObject mod = avObjects.get(0);
		          //修改为离开状态
					mod.put("state", 3);
					final int number = mod.getNumber("number").intValue();
					mod.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								Toast.makeText(VerifyInformation.this,"允许车辆离开", Toast.LENGTH_SHORT).show();
								//将车位主的车位设置为可用
								AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
								query.whereEqualTo("objectId", mod.getString("spaceid"));
								query.findInBackground(new FindCallback<AVObject>() {
									@Override
								    public void done(List<AVObject> avObjects, AVException e) {
								        if (e == null) {
								            AVObject mod = avObjects.get(0);
								            String ownerid = mod.getString("ownerid");
								            //修改为可用状态
											mod.put("state", 0);
											mod.saveInBackground();
											//推送车主已离开小区信息到业主手机
											AVQuery<AVObject> query = new AVQuery<AVObject>("OwnerInfo");
								    		query.whereEqualTo("objectId", ownerid);
								    		query.findInBackground(new FindCallback<AVObject>() {
								    			@Override
								    		    public void done(List<AVObject> avObjects, AVException e) {
								    		        if (e == null) {
								    		            AVObject owner = avObjects.get(0);
								    		            String installationId = owner.getString("installationid");
								    		            AVPush push = new AVPush();
												        // 设置频道
												        push.setChannel("public");
												        // 设置消息
												        String msg = ""+number+"号车位预订车主已离开";
												        push.setMessage(msg);
												        push.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",installationId));
												        // 推送
												        push.sendInBackground(new SendCallback() {
												          @Override
												          public void done(AVException e) {
												            if (e == null) {
												              Toast.makeText(VerifyInformation.this, "已经通知业主车位已空闲", Toast.LENGTH_SHORT).show();
												            } else {
												              Toast.makeText(VerifyInformation.this, "Send fails with :" + e.getMessage(), Toast.LENGTH_LONG).show();
												            }
												          }
												        });
								    		        }
								    			}
								    		});
								        }
									}
								});
								//获取最新已发布车位信息
					        } else {
					        	Toast.makeText(VerifyInformation.this,"上传数据出错，请重试", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }else {
		        	Toast.makeText(VerifyInformation.this,"不允许车辆离开", Toast.LENGTH_SHORT).show();
		        }
			}
		});
	}
	
	
	//注销
	public void logoutFun(View v) {
		Intent intent = new Intent(VerifyInformation.this, Login.class);
		startActivity(intent);
		finish();
	}
}
