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
	//ÿСʱ���𣬳�ʱ�������ͷ�����
	private int finePerHour = 0, total_minute = 0, total_fine = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//ȥ������
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
		    		//��ѯСʱ����
		    		AVQuery<AVObject> query_space = new AVQuery<AVObject>("SpaceInfo");
		    		query_space.whereEqualTo("objectId", obj.getString("spaceid"));
		    		query_space.findInBackground(new FindCallback<AVObject>() {
						@Override
					    public void done(List<AVObject> avObjects, AVException e) {
					        if (e == null) {
					            AVObject space = avObjects.get(0);
					            finePerHour = space.getNumber("fine").intValue();
					            fine.setText(space.getNumber("fine").toString()+"Ԫ");
					        }
						}
					});
		    		//��ȡ��ţ���ʼʱ�䣬����ʱ��
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
	 * @function ��ʱ�뿪С��
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
		            	return; // δ��ʱ
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
	 * ��������
	 */
	final Handler showDialog = new Handler(){
    	@Override
		public void handleMessage(Message msg)
		{
			if(msg.what==0x00){
		    	String message = "��λ������ʱ"+ total_minute + "���ӣ�������Ϊ"+ total_fine + "Ԫ���������Ͻɷѣ�";
		    	new AlertDialog.Builder(LeaveVerify.this).setTitle("��ʱ����֪ͨ").setMessage(message)
				.setPositiveButton("���Ͻɷ�", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
				        overtimeFine();
					};
						}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).show();
			}
		}
    };
	
    /**
     * ��ʱ����
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
		            // �ڶ�����־�����ӷ���ͻ��ѽ��
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
		    		            	Toast.makeText(LeaveVerify.this, "�������������ʧ��", Toast.LENGTH_SHORT).show();
		    		            } else {
		    		            //�۳�������Ǯ
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
					    					//���ӳ�λ����Ǯ
					    					owner.findInBackground(new FindCallback<AVObject>() {
					    						@Override
					    					    public void done(List<AVObject> avObjects, AVException e) {
					    					        if (e == null) {
					    					        	AVObject modCost = avObjects.get(0);
					    					        	modCost.increment("money", total_fine);
					    					        	modCost.saveInBackground();
					    					        	Toast.makeText(LeaveVerify.this, "�������ύ�ɹ������Է���", Toast.LENGTH_SHORT).show();
					    					        } else {
					    					        	Log.d("OwnerInfo", "���moneyʧ��"+e.getMessage());
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
	 * @function ��ʱ�뿪С��
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
		            //�޸�Ϊ�뿪״̬
					mod.put("state", 3);
					final int number = mod.getNumber("number").intValue();
					mod.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								//����λ���ĳ�λ����Ϊ����
								AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
								query.whereEqualTo("objectId", mod.getString("spaceid"));
								query.findInBackground(new FindCallback<AVObject>() {
									@Override
								    public void done(List<AVObject> avObjects, AVException e) {
								        if (e == null) {
								            AVObject mod = avObjects.get(0);
								            ownerid = mod.getString("ownerid");
								            //�޸�Ϊ����״̬
											mod.put("state", 0);
											mod.saveInBackground();
											//���ͳ������뿪С����Ϣ��ҵ���ֻ�
											AVQuery<AVObject> query = new AVQuery<AVObject>("OwnerInfo");
								    		query.whereEqualTo("objectId", ownerid);
								    		query.findInBackground(new FindCallback<AVObject>() {
								    			@Override
								    		    public void done(List<AVObject> avObjects, AVException e) {
								    		        if (e == null) {
								    		            AVObject owner = avObjects.get(0);
								    		            String installationId = owner.getString("installationid");
								    		            AVPush push = new AVPush();
												        // ����Ƶ��
												        push.setChannel("public");
												        // ������Ϣ
												        String msg = ""+number+"�ų�λԤ���������뿪";
												        push.setMessage(msg);
												        push.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",installationId));
												        // ����
												        push.sendInBackground(new SendCallback() {
												          @Override
												          public void done(AVException e) {
												            if (e == null) {
												              Toast.makeText(LeaveVerify.this, "�Ѿ�֪ͨҵ����λ�ѿ���", Toast.LENGTH_SHORT).show();
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
								//��ȡ�����ѷ�����λ��Ϣ
					        } else {
					        	Toast.makeText(LeaveVerify.this,"�ϴ����ݳ���������", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }else {
		        	Toast.makeText(LeaveVerify.this,"���������뿪", Toast.LENGTH_SHORT).show();
		        }
			}
		});
	}
}

