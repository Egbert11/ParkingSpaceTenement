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
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_verify_information);
		
		userName = (TextView)findViewById(R.id.textView1);
		number = (TextView)findViewById(R.id.textView6);
		address = (TextView)findViewById(R.id.textView7);
		time = (TextView)findViewById(R.id.textView8);
		pay = (TextView)findViewById(R.id.textView9);
		
		userName.setText("�û�����"+CommunityInfo.getUserName());
		Bundle bun = this.getIntent().getExtras();
		objId = bun.getString("info");
		Toast.makeText(VerifyInformation.this, objId, Toast.LENGTH_SHORT).show();
		
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
		    		//��ȡ��ţ�ʱ���Լ�����
		    		number.setText(obj.getNumber("number").toString());
		    		time.setText(obj.getNumber("start").toString()+":00--"+obj.getNumber("end").toString()+":00");
		    		pay.setText(obj.getNumber("cost").toString()+"Ԫ");
		        } else {
		        
		        }
			}
		});
	}
	
	//����С��
	public void enterFun(View v) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		Log.d("objectid", objId);
		
		query.whereEqualTo("objectId", objId);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("�޸ĳɹ�", "��ѯ��" + avObjects.size() + " ����������������");
		            AVObject mod = avObjects.get(0);
		          //�޸�Ϊ����״̬
					mod.put("state", 2);
					mod.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								Toast.makeText(VerifyInformation.this,"����������", Toast.LENGTH_SHORT).show();
								//��ȡ�����ѷ�����λ��Ϣ
					        } else {
					        	Toast.makeText(VerifyInformation.this,"�ϴ����ݳ���������", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }else {
		        	Toast.makeText(VerifyInformation.this,"������������", Toast.LENGTH_SHORT).show();
		        }
			}
		});
	}
	
	//�뿪С��
	public void leaveFun(View v) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		Log.d("objectid", objId);
		
		query.whereEqualTo("objectId", objId);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("�޸ĳɹ�", "��ѯ��" + avObjects.size() + " ����������������");
		            final AVObject mod = avObjects.get(0);
		          //�޸�Ϊ�뿪״̬
					mod.put("state", 3);
					final int number = mod.getNumber("number").intValue();
					mod.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								Toast.makeText(VerifyInformation.this,"�������뿪", Toast.LENGTH_SHORT).show();
								//����λ���ĳ�λ����Ϊ����
								AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
								query.whereEqualTo("objectId", mod.getString("spaceid"));
								query.findInBackground(new FindCallback<AVObject>() {
									@Override
								    public void done(List<AVObject> avObjects, AVException e) {
								        if (e == null) {
								            AVObject mod = avObjects.get(0);
								            String ownerid = mod.getString("ownerid");
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
												              Toast.makeText(VerifyInformation.this, "�Ѿ�֪ͨҵ����λ�ѿ���", Toast.LENGTH_SHORT).show();
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
								//��ȡ�����ѷ�����λ��Ϣ
					        } else {
					        	Toast.makeText(VerifyInformation.this,"�ϴ����ݳ���������", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }else {
		        	Toast.makeText(VerifyInformation.this,"���������뿪", Toast.LENGTH_SHORT).show();
		        }
			}
		});
	}
	
	
	//ע��
	public void logoutFun(View v) {
		Intent intent = new Intent(VerifyInformation.this, Login.class);
		startActivity(intent);
		finish();
	}
}
