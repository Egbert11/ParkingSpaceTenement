package com.zhcs.manage;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zhcs.community.CommunityInfo;
import com.zhcs.community.SpaceManageBean;

public class GetManageInfo {
	private static ArrayList<SpaceManageBean> list = new ArrayList<SpaceManageBean>();
	private static Activity activity = null;
	public static void getSpaceInfo(Activity act) {
		activity = act;
		if(!list.isEmpty())
			list.clear();
		String communityid = CommunityInfo.getId();
		AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
		query.whereEqualTo("communityid", communityid);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException e) {
				if (e == null) {
					final int size = arg0.size();
					for(int i = 0; i < arg0.size(); i++) {
						final int j = i;
						AVObject obj = arg0.get(i);
						final SpaceManageBean bean = new SpaceManageBean();
						//设置车位号
						bean.setNumber(obj.getNumber("num").intValue());
						//设置车位状态
						bean.setState(obj.getNumber("state").intValue());
						String ownerid = obj.getString("ownerid");
						//设置车位所有者
						final String spaceid = obj.getObjectId();
						AVQuery<AVObject> query = new AVQuery<AVObject>("OwnerInfo");
						query.whereEqualTo("objectId", ownerid);
						query.findInBackground(new FindCallback<AVObject>() {
							@Override
							public void done(List<AVObject> arg0, AVException e) {
								if (e == null) {
										AVObject owner = arg0.get(0);
										bean.setOwner(owner.getString("name"));
										//设置车位成交笔数
										AVQuery<AVObject> dealCount = new AVQuery<AVObject>("BookSpaceLog");
										dealCount.whereEqualTo("spaceid", spaceid);
										dealCount.findInBackground(new FindCallback<AVObject>() {
											@Override
											public void done(List<AVObject> arg0, AVException e) {
												if (e == null) {
														bean.setDealNum(arg0.size());
														list.add(bean);
														if(j == size - 1) {
															Intent intent = new Intent(activity, ManageMap.class);
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
					}
				} else {
					
				}
			}
		});
	}
	public static ArrayList<SpaceManageBean> getList() {
		return list;
	}
	public static void setList(ArrayList<SpaceManageBean> list) {
		GetManageInfo.list = list;
	}
}
