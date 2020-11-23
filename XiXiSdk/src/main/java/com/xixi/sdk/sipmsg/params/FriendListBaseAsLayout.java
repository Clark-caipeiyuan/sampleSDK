package com.xixi.sdk.sipmsg.params;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap.XCAP_REQUEST_TYPE;
import com.xixi.sdk.controller.xcap.LLXcapRequest;
import com.xixi.sdk.controller.xcap.LLXcapUtil;
import com.xixi.sdk.model.LLBuddy;
import com.xixi.sdk.model.LLXmlNode;
import com.xixi.sdk.observer.IICleanable;
import com.xixi.sdk.observer.LLCleanResourceKits;
import com.xixi.sdk.observer.LLMeta;
import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.parser.XmlParser;
import com.xixi.sdk.utils.download.LLCursorWithTimes;
import com.xixi.sdk.utils.file.IoCompletionListener;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.file.LLFileUtils;
import com.xixi.sdk.utils.network.LLCallback;
import com.xixi.sdk.utils.network.LLException.LLNetworkException;
import com.xixi.sdk.utils.pingyin.PinyinComparator;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public abstract class FriendListBaseAsLayout extends LLMeta implements IICleanable {
	public interface FriendListLoadingListener {
		public void onStartOfFriendList();

		public void onLoadingOfFriendList();

		public void onFinishLoadingOfFriendList(boolean success, String reasonIfFailed);
	}

	final FriendListLoadingListener defaultListener = new FriendListLoadingListener() {

		@Override
		public void onStartOfFriendList() {
		}

		@Override
		public void onLoadingOfFriendList() {

		}

		@Override
		public void onFinishLoadingOfFriendList(boolean success, String reasonIfFailed) {

		}
	};

	FriendListLoadingListener _internalEvents = new FriendListLoadingListener() {

		@Override
		public void onStartOfFriendList() {
			_loadingInProgress = true;
			_loadingEventListener.onStartOfFriendList();
		}

		@Override
		public void onLoadingOfFriendList() {
			_loadingEventListener.onLoadingOfFriendList();
		}
		@Override
		public void onFinishLoadingOfFriendList(boolean success, String reasonIfFailed) {
			_loadingInProgress = false;
			_loadingEventListener.onFinishLoadingOfFriendList(success, reasonIfFailed);
		}
	};

	FriendListLoadingListener _loadingEventListener = defaultListener;

	public void setLoadingEventListener(FriendListLoadingListener eventLis) {
		this._loadingEventListener = eventLis == null ? defaultListener : eventLis;
	}

	boolean _loadingInProgress = false;

	public void loadDataOverXcap() {
		android.util.Log.i(LongLakeApplication.DANIEL_TAG,"load data over xcap ");
		if (is_loadContactsLocally()) {
			Runnable r = new Runnable() {
				public void run() {
					Log.i(LongLakeApplication.DANIEL_TAG, "a try , but failed");
					loadDataOverXcap();
				}
			};
			UIThreadDispatcher.dispatch(r, 1000);
			return;
		}

		if (_loadingInProgress) {
			return;
		}
		_internalEvents.onStartOfFriendList();
		LLXcapUtil.getInstance().getContactsVersion(getUniqueId(),getSipAddr(),new IoCompletionListener1<String>() {
			@Override
			public void onFinish(final String remoteVer, Object context) {
				final String userId = getUniqueId();
				Log.i(LongLakeApplication.DANIEL_TAG, String.format("ver=>%s , %s", mData.getVersion(), remoteVer));
				//if (LLSDKUtils.compareTo(mData.getVersion(), remoteVer) < 0) {
					if (!TextUtils.equals((mData.getVersion()), remoteVer)) {
//					if (!TextUtils.equals((mData.getVersion()), "0")) {

					IoCompletionListener<List<LLXmlNode>> ioCall = new IoCompletionListener<List<LLXmlNode>>() {
						@Override

						public void onError(String errReason, final Throwable exceptionInstance) {
							_internalEvents.onFinishLoadingOfFriendList(false, errReason);
						}

						@Override
						public void onFinish(final List<LLXmlNode> data, Object context) {

							for(int i=0;i<data.size();i++){
								Log.d("clark"+i,new Gson().toJson(data.get(i)));   //获取数据
							}
							if (TextUtils.equals(userId, getUniqueId())) {
								LLStatusTag tag = new LLStatusTag() ; 
								tag.makeChangeTag(true) ; 
								_loadMenus(data, userId, remoteVer , tag);
							} else {
								onError("incorrect user id", new RuntimeException("incorrect user id"));
							}
						}

					};
					LLNetworkOverXcap.getInstance()
							.routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_TOTAL_LAYOUT,
									getSipAddr(),getUniqueId(), ioCall, null));
				} else {
					_loadMenus(mData.getLayoutData(), userId, remoteVer, new LLStatusTag());
				}
			}

		});
	}

	private void _loadMenus(final List<LLXmlNode> raw_data, final String originalUserId, final String remoteV , LLStatusTag statusTag) {
		final List<LLXmlNode> result_list = new ArrayList<LLXmlNode>();
		_loadMenu(raw_data, new LLCursorWithTimes(), result_list, originalUserId, remoteV, statusTag);
	}

	private static class LLStatusTag {

		public LLStatusTag() {
			oerTag = 0;
		}

		public LLStatusTag makeTagWithNetworkError(boolean error) {
			oerTag = (oerTag | (error ? 1 : 0));
			return this;
		}

		public LLStatusTag makeChangeTag(boolean changed) {
			oerTag = (oerTag | (changed ? 16 : 0));
			return this;
		}

		public boolean containNetworkError() {
			return 0 != (oerTag & 0xF);
		}

		public boolean containChangeTag() {
			return 0 != (oerTag & 0xF0);
		}

		private int oerTag;

		public int getOerTag() {
			return oerTag;
		}

		public void setOerTag(int oerTag) {
			this.oerTag = oerTag;
		}
	}

	private void _loadInfoRemotely(final String devId, final List<LLXmlNode> src, final LLCursorWithTimes cursor,
			final List<LLXmlNode> result_list, final String remoteV, final LLStatusTag _successBit) {
		int index = cursor.getCurrentPosition();
		final LLXmlNode lxn = src.get(index);
		final String userId = getUniqueId();
		IoCompletionListener<String> ioCall = new IoCompletionListener<String>() {
			@Override
			public void onError(String errReason, final Throwable exceptionInstance) {
				Log.i(LongLakeApplication.DANIEL_TAG, "menu info failed: " + errReason);
				if (exceptionInstance instanceof LLNetworkException) {
					if (cursor.get_times() == 0) {
						cursor.increTimesByOne();
						_loadMenu(src, cursor, result_list, userId, remoteV, _successBit);
					} else {
						cursor.moveToNext();
						_loadMenu(src, cursor, result_list, userId, remoteV, _successBit.makeTagWithNetworkError(true));
					}
				} else {
					cursor.moveToNext();
					_loadMenu(src, cursor, result_list, userId, remoteV, _successBit);
				}
			}

			@Override
			public void onFinish(String result, Object context) {
				Log.e("clark",getUniqueId()+"---"+userId);   //进行数据的写入在这里
				if (!TextUtils.equals(getUniqueId(), userId)) {
					_internalEvents.onFinishLoadingOfFriendList(false, "incorrect user id");
				} else {
					android.util.Log.i(LongLakeApplication.DANIEL_TAG,"load info onFinish : " + result);
					LLXmlNode xmlNode = XmlParser.parseXml(result, LLBuddy.getLayoutMapInstanceCallback(),
							LLBuddy.getBuddyKeys());
					List<LLXmlNode> buddyNode = xmlNode.getChildren().get(LLBuddy.BUDDY_NAME);
					if (buddyNode.size() != 0) {
						LLBuddy buddy = (LLBuddy) buddyNode.get(0);
						processLLBuddy(buddy);
						String expdate = ((LLBuddy)lxn ).getAppAccessExpDate();
						buddy.setAppAccessExpDate(expdate);
						String expireDate = ((LLBuddy)lxn).getExpireDate(); 
						buddy.setExpireDate(expireDate);
						String floor =  ((LLBuddy)lxn).getFloor(); 
						buddy.setFloor(floor);
						result_list.add(buddy);
						LLFileUtils.getInstance().writeToFile(devId,
								LLGsonUtils.getInstance().toJson(new LLInfoDetails(buddy.getVer(),
							    LLGsonUtils.getInstance().toJson(buddy))));
					}

					cursor.moveToNext();
					_loadMenu(src, cursor, result_list, userId, remoteV, _successBit);

				}
			}

		};

		LLNetworkOverXcap.getInstance().routeRequest(
				new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_DETAILS,getSipAddr(), "", lxn.getId(), ioCall, null));
	}

	private void _loadMenu(final List<LLXmlNode> src, final LLCursorWithTimes cursor, final List<LLXmlNode> result_list,
			final String userId, final String remoteV, final LLStatusTag _successBit) {
		int index = cursor.getCurrentPosition();
		if (index < src.size() && !_successBit.containNetworkError()) {
			final LLXmlNode lxn = src.get(index);
			final String devId = "info" + lxn.getId();
			LLFileUtils.getInstance().readFromFile(devId, new IoCompletionListener1<String>() {
				public void onFinish(String result, Object context) {
					final LLInfoDetails _detail = (LLInfoDetails) LLGsonUtils.fromJson(result, LLInfoDetails.class);
					if (_detail == null) {
						_loadInfoRemotely(devId, src, cursor, result_list, remoteV, _successBit.makeChangeTag(true));
					} else {
						LLCallback<ResponseBody> callback = new LLCallback<ResponseBody>() {
							@Override
							public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
								if (arg1 instanceof LLNetworkException) {
									cursor.moveToNext();
									_loadMenu(src, cursor, result_list, userId, remoteV,
											_successBit.makeTagWithNetworkError(true));
								} else {
									Log.e("clark","save menu");
									LLBuddy buddy = (LLBuddy)LLGsonUtils.fromJson(_detail.getContent(), LLBuddy.class);
									if(buddy != null){
										String expdate = ((LLBuddy)lxn ).getAppAccessExpDate();
										String expireDate = ((LLBuddy)lxn).getExpireDate();
										String floor = ((LLBuddy)lxn).getFloor() ;
										if((!TextUtils.equals(buddy.getAppAccessExpDate(),expdate)) || 
											(!TextUtils.equals(buddy.getExpireDate(),expireDate)) || 
											(!TextUtils.equals(buddy.getFloor(),floor)) ){
											buddy.setAppAccessExpDate(expdate);
											buddy.setExpireDate(expireDate);
											buddy.setFloor(floor);
											_successBit.makeChangeTag(true);
											LLFileUtils.getInstance().writeToFile(devId,
													LLGsonUtils.getInstance().toJson(new LLInfoDetails(buddy.getVer(),
															LLGsonUtils.getInstance().toJson(buddy))));
										}
										processLLBuddy((LLBuddy)buddy);
										result_list.add(buddy);
										cursor.moveToNext();
										_loadMenu(src, cursor, result_list, userId, remoteV, _successBit);
									}else{
										_loadInfoRemotely(devId, src, cursor, result_list, remoteV, _successBit.makeChangeTag(true));
									}
								}
							}

							@Override
							public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1,
									final String _infoVer) {
								if (LLSDKUtils.compareTo(_detail.getVer(), _infoVer) < 0) {
									_loadInfoRemotely(devId, src, cursor, result_list, remoteV,
											_successBit.makeChangeTag(true));
								} else {
									LLBuddy buddy = (LLBuddy)LLGsonUtils.fromJson(_detail.getContent(), LLBuddy.class);
									if(buddy != null){
										String expdate = ((LLBuddy)lxn ).getAppAccessExpDate();
										String expireDate = ((LLBuddy)lxn).getExpireDate(); 
										String floor =  ((LLBuddy)lxn).getFloor(); 
										if((!TextUtils.equals(buddy.getAppAccessExpDate(),expdate)) || 
											(!TextUtils.equals(buddy.getExpireDate(),expireDate)) || 
											(!TextUtils.equals(buddy.getFloor(),floor))){
											buddy.setAppAccessExpDate(expdate);
											buddy.setExpireDate(expireDate);
											buddy.setFloor(floor);
											_successBit.makeChangeTag(true);
											Log.e("clark","save menu");
											LLFileUtils.getInstance().writeToFile(devId,
													LLGsonUtils.getInstance().toJson(new LLInfoDetails(buddy.getVer(),
															LLGsonUtils.getInstance().toJson(buddy))));
										}
										processLLBuddy((LLBuddy)buddy);
										result_list.add(buddy);
										cursor.moveToNext();
										_loadMenu(src, cursor, result_list, userId, remoteV, _successBit);
									}else{
										_loadInfoRemotely(devId, src, cursor, result_list, remoteV, _successBit.makeChangeTag(true));
									}
								}
							}
						};
						LLNetworkOverXcap.getInstance()
								.routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_DOOR_DETAILS_VERSION,
										getSipAddr(), lxn.getId(), callback));
					}
				}
			});
		} else {
			if (_successBit.containChangeTag() && !_successBit.containNetworkError()) {
				Collections.sort(result_list, PinyinComparator.getInstance());
				_setData(result_list, remoteV);
			} else {
				Log.i(LongLakeApplication.DANIEL_TAG, "stop updating");
			}
			uploadLog();
			_internalEvents.onFinishLoadingOfFriendList(!_successBit.containNetworkError(), "");
		}
	}
	public String getStoredFile() {
		return "friend_layout.json";
	};
	public FriendListBaseAsLayout() {
		LLCleanResourceKits.getInstance().registerAsObserver(this, true);
	}
    public abstract String getUniqueId();
    public abstract String getSipAddr();
    public abstract void processLLBuddy(LLBuddy buddy);
    public abstract void readFromRecentDB(IoCompletionListener1<String> ioCompletionListener1);
    public abstract boolean updateRecentList();
    public abstract void uploadLog();

	private static class VersionData {
		public final static String DEFAULT_VER = "-1";
		protected String version = DEFAULT_VER;

		public String getVersion() {
			return version;
		}

		public void setVersion(Long version) {
			this.version = String.valueOf(version);
		}

		public void setVersion(String version) {
			this.version = TextUtils.isEmpty(version) ? this.version : version;
		}
	}

	public static class LayoutData extends VersionData implements IICleanable, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2263210255521192879L;

		public LayoutData() {
			super();
			layoutData = new ArrayList<LLXmlNode>();
		}

		List<LLXmlNode> layoutData;

		public List<LLXmlNode> getLayoutData() {
			return layoutData;
		}

		public void setLayoutData(List<LLXmlNode> layoutData) {
			if (this.layoutData != layoutData) {
				if (layoutData != null) {
					this.layoutData = layoutData;
				} else {
					this.layoutData.clear();
				}

			}
		}

		@Override
		public void cleanCache(Object o) {
			layoutData.clear();
			version = DEFAULT_VER;
		}
	};

	boolean _loadContactsLocally = false;

	private boolean is_loadContactsLocally() {
		return _loadContactsLocally;
	}

	private void set_loadContactsLocally(boolean _loadContactsLocally) {
		Log.i(LongLakeApplication.DANIEL_TAG, "set to " + Boolean.valueOf(_loadContactsLocally).toString());
		this._loadContactsLocally = _loadContactsLocally;
	}

	protected void readFromDB() {
        if(TextUtils.isEmpty(getUniqueId())){
        	return ;
        }
		set_loadContactsLocally(true);
		final String uniqueId = getUniqueId();
		LLFileUtils.getInstance().readFromFile(getStoredFile(), new IoCompletionListener1<String>() {
			@Override
			public void onFinish(String data, Object context) {
				LLGsonUtils.getInstance().fromJson(data, LayoutData.class, new IoCompletionListener1<Object>() {
					public void onFinish(final Object versionData, Object context) {

						if (!TextUtils.equals(uniqueId, getUniqueId())) {
							set_loadContactsLocally(false);
							return;
						}

						if (versionData == null) {
							mData = new LayoutData();
							mData.setVersion("");
						} else {
							mData = (LayoutData) versionData;
						}
                        Log.i(LongLakeApplication.DANIEL_TAG,"read from DB: " + mData.getLayoutData().size());
						notifyOb(new Object[] { mData, null });
						readFromRecentDB(new IoCompletionListener1<String>() {
								@Override
								public void onFinish(String data, Object context) {
									set_loadContactsLocally(false);
								}
							});
					}
				});

			}
		});
	}
	protected void backupToDB() {
		String strJson = LLGsonUtils.getInstance().toJson(mData);
		LLFileUtils.getInstance().writeToFile(this.getStoredFile(), strJson);
	}

	private final LayoutData null_layoutData = new LayoutData();
	public LayoutData mData = null_layoutData;

	public String getVer() {
		return mData.getVersion();
	}

	public List<LLXmlNode> getData() {
		return mData.getLayoutData();
	}

	public LLBuddy getBuddybyId(String id){
		for(LLXmlNode node : getData()){
			if(TextUtils.equals(node.getId(),id)){
				return (LLBuddy)node ;
			}
		}
		return null ;
	}
	protected void _setData(List<LLXmlNode> bds, String v) {

		if (mData == null_layoutData) {
			mData = new LayoutData();
		}
		
		mData.setVersion(v);
		mData.setLayoutData(bds);
		
		this.notifyOb(new Object[] { mData, null });
		backupToDB();
	    updateRecentList();
	}

	@Deprecated
	public void setData(Object ob) {

	}
	@Override
	public Object getDataWithCloneVersion() {
		return null;
	}
	@Override
	public void cleanCache(Object o) {
		UIThreadDispatcher.dispatch(new Runnable() {
			public void run() {
				LLFileUtils.getInstance().rvFile(getStoredFile());
				mData.cleanCache(null);
			}
		});
	}
}
