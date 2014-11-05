package com.zhcs.manage;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zhcs.community.CommunityInfo;
import com.zhcs.community.SpaceHistoryBean;

public class GetHistoryInfo {
	private static ArrayList<SpaceHistoryBean> list = new ArrayList<SpaceHistoryBean>();
	private static Activity activity = null;
	public static void getSpaceInfo(Activity act) {
		activity = act;
		if(!list.isEmpty())
			list.clear();
		String communityid = CommunityInfo.getId();
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		query.whereEqualTo("communityid", communityid);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException e) {
				if (e == null) {
					final int size = arg0.size();
					for(int i = 0; i < size; i++) {
						final int j = i;
						AVObject obj = arg0.get(i);
						final SpaceHistoryBean bean = new SpaceHistoryBean();
						//设置车位号
						bean.setNumber(obj.getNumber("number").intValue());
						//设置成交价格
						bean.setCost(obj.getNumber("cost").intValue());
						//设置时间
						bean.setDate(obj.getCreatedAt());
						bean.setStart(obj.getNumber("start").intValue());
						bean.setEnd(obj.getNumber("end").intValue());
						//设置车位所有者
						Log.e("history","1"+arg0.size());
						final String spaceid = obj.getString("spaceid");
						AVQuery<AVObject> query01 = new AVQuery<AVObject>("SpaceInfo");
						query01.whereEqualTo("objectId", spaceid);
						query01.findInBackground(new FindCallback<AVObject>() {
							@Override
							public void done(List<AVObject> arg0, AVException e) {
								if (e == null) {
									AVObject obj = arg0.get(0);
									Log.e("history","2"+arg0.size());
									String ownerid = obj.getString("ownerid");
									AVQuery<AVObject> query02 = new AVQuery<AVObject>("OwnerInfo");
									query02.whereEqualTo("objectId", ownerid);
									query02.findInBackground(new FindCallback<AVObject>() {
										@Override
										public void done(List<AVObject> arg0, AVException e) {
											if (e == null) {
													AVObject owner = arg0.get(0);
													Log.e("history","3"+arg0.size());
													bean.setOwner(owner.getString("name"));
													//设置车位成交笔数
													AVQuery<AVObject> dealCount = new AVQuery<AVObject>("BookSpaceLog");
													dealCount.whereEqualTo("spaceid", spaceid);
													dealCount.findInBackground(new FindCallback<AVObject>() {
														@Override
														public void done(List<AVObject> arg0, AVException e) {
															if (e == null) {
																Log.e("history","4"+arg0.size());
																bean.setDealNum(arg0.size());
																list.add(bean);
																Log.e("history","5"+list.size());
																if(j == size - 1) {
																	Intent intent = new Intent(activity, HistoryMap.class);
																	activity.startActivity(intent);
																}
															} else {
																
															}
														}
													});
											} else {
												
											}
										}
									});
								}else {
									
								}
							}
						});
					}
					
				} else {
					
				}
			}
		});
		
	}
	public static ArrayList<SpaceHistoryBean> getList() {
		return list;
	}
	public static void setList(ArrayList<SpaceHistoryBean> list) {
		GetHistoryInfo.list = list;
	}
}
