package com.zhcs.manage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class LeaveVerify extends Activity{
	private TextView address, number, start_time, end_time, fine;
	private String objId = "", ownerid = "";
	//每小时罚金，超时分钟数和罚款金额
	private int finePerHour = 0, total_minute = 0, total_fine = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_leave_verify);
		
		address = (TextView)findViewById(R.id.community_name);
		number = (TextView)findViewById(R.id.space_number);
		start_time = (TextView)findViewById(R.id.start_time);
		end_time = (TextView)findViewById(R.id.end_time);
		fine = (TextView)findViewById(R.id.fine);
		
		Bundle bun = this.getIntent().getExtras();
		objId = bun.getString("info");
		
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
		    		//查询小时罚金
		    		AVQuery<AVObject> query_space = new AVQuery<AVObject>("SpaceInfo");
		    		query_space.whereEqualTo("objectId", obj.getString("spaceid"));
		    		query_space.findInBackground(new FindCallback<AVObject>() {
						@Override
					    public void done(List<AVObject> avObjects, AVException e) {
					        if (e == null) {
					            AVObject space = avObjects.get(0);
					            finePerHour = space.getNumber("fine").intValue();
					            fine.setText(space.getNumber("fine").toString()+"元");
					        }
						}
					});
		    		//获取编号，开始时间，结束时间
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
	 * @function 超时离开小区
	 */
	public void overtime(View v) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		Log.d("objectid", objId);
		
		query.whereEqualTo("objectId", objId);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            final AVObject spacelog = avObjects.get(0);
		            Date endtime = spacelog.getDate("end");
		            Calendar end = Calendar.getInstance();
		            Calendar cur = Calendar.getInstance();
		            end.setTime(endtime);
		            cur.setTime(new Date());
		            if(end.get(Calendar.HOUR_OF_DAY) > cur.get(Calendar.HOUR_OF_DAY) || 
		            		(end.get(Calendar.HOUR_OF_DAY) == cur.get(Calendar.HOUR_OF_DAY) &&
		            				end.get(Calendar.MINUTE) >= cur.get(Calendar.MINUTE))) {
		            	return; // 未超时
		            }
		            else{
		            	total_minute = cur.get(Calendar.HOUR_OF_DAY) * 60 + cur.get(Calendar.MINUTE) - 
		            			(end.get(Calendar.HOUR_OF_DAY) * 60 + end.get(Calendar.MINUTE));
		            	total_fine = (int)(((float)finePerHour / 2) * (int)((total_minute + 30) / 30));
		            	Message msg = new Message();
		            	msg.what = 0x00;
		            	showDialog.sendMessage(msg);
		            	
		            }
		            
		        }
			}
		});
	}
	
	/**
	 * 罚款提醒
	 */
	final Handler showDialog = new Handler(){
    	@Override
		public void handleMessage(Message msg)
		{
			if(msg.what==0x00){
		    	String message = "这位车主超时"+ total_minute + "分钟，罚款金额为"+ total_fine + "元。现在马上缴费？";
		    	new AlertDialog.Builder(LeaveVerify.this).setTitle("超时罚款通知").setMessage(message)
				.setPositiveButton("马上缴费", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
				        overtimeFine();
					};
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).show();
			}
		}
    };
	
    /**
     * 超时罚款
     */
    private void overtimeFine() {
    	AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		Log.d("objectid", objId);
		query.whereEqualTo("objectId", objId);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            final AVObject spacelog = avObjects.get(0);
		            // 在订单日志上增加罚款和花费金额
		            spacelog.increment("fine", total_fine);
		            spacelog.increment("cost", total_fine);
		            spacelog.saveInBackground();
		            
		            final String spaceid = spacelog.getString("spaceid"); 
		            
		            AVQuery<AVObject> query = new AVQuery<AVObject>("DriverInfo");
		    		query.whereEqualTo("objectId", spacelog.getString("driverid"));
		    		query.findInBackground(new FindCallback<AVObject>() {
		    			@Override
		    		    public void done(List<AVObject> avObjects, AVException e) {
		    		        if (e == null) {
		    		            AVObject mod = avObjects.get(0);
		    		            if(mod.getInt("money") < total_fine) {
		    		            	Toast.makeText(LeaveVerify.this, "你的余额不够，罚款失败", Toast.LENGTH_SHORT).show();
		    		            } else {
		    		            //扣除车主的钱
		    		            mod.increment("money", -total_fine);
		    					mod.saveInBackground();
		    					AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
								query.whereEqualTo("objectId", spaceid);
								query.findInBackground(new FindCallback<AVObject>() {
									@Override
								    public void done(List<AVObject> avObjects, AVException e) {
								        if (e == null) {
								            AVObject mod = avObjects.get(0);
								            ownerid = mod.getString("ownerid");
					    					AVQuery<AVObject> owner = new AVQuery<AVObject>("OwnerInfo");
					    					owner.whereEqualTo("objectId", ownerid);
					    					//增加车位主的钱
					    					owner.findInBackground(new FindCallback<AVObject>() {
					    						@Override
					    					    public void done(List<AVObject> avObjects, AVException e) {
					    					        if (e == null) {
					    					        	AVObject modCost = avObjects.get(0);
					    					        	modCost.increment("money", total_fine);
					    					        	modCost.saveInBackground();
					    					        	Toast.makeText(LeaveVerify.this, "罚款已提交成功，可以放行", Toast.LENGTH_SHORT).show();
					    					        } else {
					    					        	Log.d("OwnerInfo", "添加money失败"+e.getMessage());
					    					        }
					    						}
					    					});
								        }
									}
								});
		        }
			}
		}
		    		});
    }
			}
		});
    }
    
	/**
	 * 
	 * @param v
	 * @function 按时离开小区
	 */
	public void intime(View v) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		Log.d("objectid", objId);
		query.whereEqualTo("objectId", objId);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            final AVObject mod = avObjects.get(0);
		            //修改为离开状态
					mod.put("state", 3);
					final int number = mod.getNumber("number").intValue();
					mod.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								//将车位主的车位设置为可用
								AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
								query.whereEqualTo("objectId", mod.getString("spaceid"));
								query.findInBackground(new FindCallback<AVObject>() {
									@Override
								    public void done(List<AVObject> avObjects, AVException e) {
								        if (e == null) {
								            AVObject mod = avObjects.get(0);
								            ownerid = mod.getString("ownerid");
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
												              Toast.makeText(LeaveVerify.this, "已经通知业主车位已空闲", Toast.LENGTH_SHORT).show();
												            } else {
												              Toast.makeText(LeaveVerify.this, "Send fails with :" + e.getMessage(), Toast.LENGTH_LONG).show();
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
					        	Toast.makeText(LeaveVerify.this,"上传数据出错，请重试", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }else {
		        	Toast.makeText(LeaveVerify.this,"不允许车辆离开", Toast.LENGTH_SHORT).show();
		        }
			}
		});
	}
}

