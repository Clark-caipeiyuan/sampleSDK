package com.xixi.sdk.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.IElevatorDirectionListener;
import com.xixi.sdk.controller.IElevatorFloorListener;
import com.xixi.sdk.controller.ISpeechFloorVoice;
import com.xixi.sdk.controller.LLNotifier;
import com.xixi.sdk.globals.ILiftCmdCacheOpCode;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.model.LLBuddy;
import com.xixi.sdk.model.LLElevatorData;
import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.serialpos.CmdErrorCode;
import com.xixi.sdk.serialpos.ISerialDataEnum;
import com.xixi.sdk.serialpos.LLCmdsSet;
import com.xixi.sdk.serialpos.LLCmdsSet.LLProcessElevatorData;
import com.xixi.sdk.serialpos.SerialDataParser.CmdRetKits;
import com.xixi.sdk.serialpos.SerialPosClient;
import com.xixi.sdk.sipmsg.params.LLInfoDetails;
import com.xixi.sdk.sipmsg.params.LLLiftCmdCacheNode;
import com.xixi.sdk.utils.download.LLCursor;
import com.xixi.sdk.utils.download.LLCursorWithMaximum;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.file.LLCacheUtils;

import android.Manifest;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;

public class LLElevatorController extends LLNotifier<ISpeechFloorVoice>
implements ISerialDataEnum, ILiftCmdCacheOpCode {

	private HandlerThread handlerThread;
	private Handler mLiftThreadHandler;
	private static LLElevatorController instance;

	private int busyState = INIT_STATE;

	public synchronized static LLElevatorController getInstance() {
		if (instance == null) {
			instance = new LLElevatorController();
		}
		return instance;
	}

	public void test() {
		// test0() ;
		test1();
	}

	private void test0() {
		final String a[] = { "e0", "1c0", "380" };
		LLLiftCmdCache cache = new LLLiftCmdCache();
		for (String node : a) {
			cache.enqueRequest(opAllowCode, node, _default_ios);
		}
		final String b[] = { "e0", "11" };
		for (String node : b) {
			cache.enqueRequest(opDisallowCode, node, _default_ios);
		}
		cache.enqueRequest(opDirectCode, "a", _default_ios);
		cache.enqueRequest(opAllowCode, "cc", _default_ios);
		cache.enqueRequest(opAllowCode, "dd", _default_ios);
		cache.enqueRequest(opDiscardDirectCode, "ee", _default_ios);
		while (!cache.isEmpty()) {
			LLLiftCmdCacheNode ll = cache.poll();
			List<Integer> opFloorSet = ll.getOpFloorSet();
			for (int op : opFloorSet) {
			}
		}

	}

	private void test1() {

		// 567
		// 678
		// 789
		final String a[] = { "e0", "1c0", "380" };
		final LLCursor cursor = new LLCursor();
		final List<IoCompletionListener1<Boolean>> _l = new ArrayList<IoCompletionListener1<Boolean>>();

		final IoCompletionListener1<Boolean> test_io = new IoCompletionListener1<Boolean>() {

			@Override
			public void onFinish(Boolean data, Object context) {
				cursor.moveToNext();
				if (cursor.getPosition() < a.length) {

					LLElevatorController.getInstance().setAllowFloors(a[cursor.getCurrentPosition()], _l);
				}
			}
		};

		_l.add(test_io);
		LLElevatorController.getInstance().setAllowFloors(a[cursor.getCurrentPosition()], _l);
	}

	public int getState() {
		return busyState;
	}

	private int boardNum = 0;

	private void _sendElevatorSettingsWithTries(final byte[] cmd, final IoCompletionListener1<Boolean> io) {

		final LLCursorWithMaximum counter = new LLCursorWithMaximum(3);
		SerialPosClient.instance().sendDataViaCom(cmd, new IoCompletionListener1<byte[]>() {
			@Override
			public void onFinish(byte[] data, Object context) {
				final CmdErrorCode errorCode = (CmdErrorCode) context;

				if (!errorCode.needToResend()) {
					runInMagicThread(new Runnable() {
						public void run() {
							io.onFinish(errorCode.noError(), null);
						}
					});
				} else {
					counter.moveToNext();
					if (counter.hitMax()) {
						runInMagicThread(new Runnable() {
							public void run() {
								io.onFinish(false, null);
							}
						});
					} else {
						//						Log.i(LongLakeApplication.DANIEL_TAG, "try " + counter.getPosition());
						SerialPosClient.instance().sendDataViaCom(cmd, this);
					}

				}
			}
		});
	}

	public void initElevatorInfo(final LLBuddy buddy) {
		String str = LLCacheUtils.readFromFileEx(ElevatorInfoFileName, LongLakeApplication.getInstance());
		LLInfoDetails info = (LLInfoDetails) LLGsonUtils.fromJson(str, LLInfoDetails.class);
		if (info != null) {
			try {
				LLElevatorData e = (LLElevatorData) LLGsonUtils.fromJson(info.getContent(), LLElevatorData.class);
				if (LLSDKUtils.compareTo(info.getVer(), buddy.getVer()) < 0) {
					setElevatorData(e.getElevatormanaged(),buddy);
				} else {
					setElevator(info.getVer(), e);
				}
			} catch (Exception e) {
				Log.d(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
			}
		} else {
			setElevatorData(false, buddy);
		}
		initElevator(elevatorData.getBoardNum(), elevatorData.getMaxFloor(), elevatorData.getElevatormanaged());
	}

	public void initElevatorFile() { 
		String str = LLCacheUtils.readFromFileEx(ElevatorInfoFileName, LongLakeApplication.getInstance());
		LLInfoDetails info = (LLInfoDetails) LLGsonUtils.fromJson(str, LLInfoDetails.class);
		LLElevatorData e = (LLElevatorData) LLGsonUtils.fromJson(info.getContent(), LLElevatorData.class);
		LLElevatorController.getInstance().setElevator(info.getVer(), e);
		initElevator(elevatorData.getBoardNum(), elevatorData.getMaxFloor(), elevatorData.getElevatormanaged());
	}

	private void setElevatorData(final boolean managered, final LLBuddy buddy) {
		String type = buddy.getType();
		if (TextUtils.equals(type, "EL")) {
			String elevator = buddy.getElevator();
			if (!TextUtils.isEmpty(elevator)) {
				String[] str = elevator.split(",");
				StringBuilder sb = new StringBuilder();
				for (int i = 3; i < str.length; i++) {
					sb.append(str[i]);
					if (i != str.length - 1) {
						sb.append(",");
					}
				}
				String allowFloors = sb.toString();
				setElevatorData(managered,buddy.getVer(), Integer.parseInt(str[0]), Integer.parseInt(str[1]),
						Integer.parseInt(str[2]), allowFloors, buddy.getParentId());

			}
		}
		if (TextUtils.equals(type, "EM")) {
			String location = buddy.getSchedulerLocation();
			String elevatorInfo = buddy.getElevatorInfo();
			if (!TextUtils.isEmpty(location) && !TextUtils.isEmpty(elevatorInfo)) {
				setElevatorData(buddy.getVer(), buddy.getSchedulerLocation(), buddy.getElevatorInfo());
			}
		}
	}

	public void initElevator(final int boardNum, final int count, final boolean elevatorState) {
		SerialPosClient.instance().sendDataViaCom(CmdRetKits.getElevatorStatus());
		_sendElevatorSettingsWithTries(CmdRetKits.setFloorBoard(boardNum), new IoCompletionListener1<Boolean>() {

			@Override
			public void onFinish(Boolean data, Object context) {
				_sendElevatorSettingsWithTries(CmdRetKits.setFloorCount(count), new IoCompletionListener1<Boolean>() {

					@Override
					public void onFinish(Boolean data, Object context) {
						List<IoCompletionListener1<Boolean>> _l = new ArrayList<IoCompletionListener1<Boolean>>();
						_l.add(new IoCompletionListener1<Boolean>() {
							@Override
							public void onFinish(Boolean data, Object context) {
								if (data && elevatorState) {
									LLElevatorController.getInstance()
									.setAllFloorsDisallow(LLSDKUtils.num2Floors(boardNum), _default_ios);
								}
							}
						});
						LLElevatorController.getInstance().setAllFloorsAllow(LLSDKUtils.num2Floors(boardNum), _l);
					}

				});

			}
		});

	}

	public LLElevatorController() {

		// initElevatorData();
		elevatorData = defaultElevatorData;

		allowFloorState = new byte[3 + BIT_SIZE / 8];
		allowReferenceCount = new int[BIT_SIZE];
		directFloorState = new byte[3 + BIT_SIZE / 8];
		directReferenceCount = new int[BIT_SIZE];

		System.arraycopy(directFloor_magic_key, 0, directFloorState, 0, directFloor_magic_key.length);
		System.arraycopy(floorState_magic_key, 0, allowFloorState, 0, floorState_magic_key.length);

		handlerThread = new HandlerThread("send floor msg");
		handlerThread.start();
		mLiftThreadHandler = new Handler(handlerThread.getLooper());
		_default_ios.add(_default_io);

		LLCmdsSet.setProcessElevatorFloor(processElevatorData);
	}

	private final static String ElevatorInfoFileName = "elevatorInfoFile.json";

	final LLElevatorData defaultElevatorData = new LLElevatorData();
	LLElevatorData elevatorData = defaultElevatorData;

	private String version;

	public void setElevator(String ver, LLElevatorData e) {
		this.version = ver;
		this.elevatorData = e;
 	}

	public static void destineTo(Integer... args) {
		//printArray("press " , Arrays.asList(args)) ;
		LLElevatorController.getInstance().setDirectFloor(args, null);
	}

	private final LLProcessElevatorData processElevatorData = new LLProcessElevatorData() {

		@Override
		public void processElevatorDirection(final int direction) {
			LLSDKUtils.runInMainThread(new Runnable() {
				public void run() {
					setElevatorRunningStatus(logicalFloor);
				}
			});
		}

		@Override
		public void processElevatorFloor(final int _floor) {
			LLSDKUtils.runInMainThread(new Runnable() {
				public void run() {
					setElevatorRunningStatus(_floor);
				}
			});
		}
	};

	private static final int BIT_SIZE = 256;
	private byte[] floorState_magic_key = new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_ELEVATOR_ALLOW_FLOOR };
	private byte[] directFloor_magic_key = new byte[] { FRAME_ALIGNMENT_HEAD_VALUE, 0, CMD_ELEVATOR_DIRECT_FLOOR };

	private byte[] allowFloorState = null;
	private int allowReferenceCount[] = null;
	private byte[] directFloorState = null;
	private int directReferenceCount[] = null;

	private static void printArray(int prefix, final List<Integer> floors) {
		printArray(LiftOpCodeName[prefix], floors);
	}

	private static void printArray(String prefix, final List<Integer> floors) {

		StringBuilder sb = new StringBuilder(prefix);
		for (Integer i : floors) {
			sb.append(String.format(" %d", i));
		}
		Log.i(LongLakeApplication.DANIEL_TAG, sb.toString());
	}

	private class BusyTag {

		private boolean deviceBusy = false;

		public boolean isDeviceBusy() {
			return deviceBusy;
		}

		public void setDeviceBusy(boolean deviceBusy) {
			// Log.i(LongLakeApplication.DANIEL_TAG, "set busy to " +
			// Boolean.valueOf(deviceBusy).toString());
			this.deviceBusy = deviceBusy;
		}

		public void testBusyBitAndRun(int opCode, final Runnable r, final List<Integer> floors,
				final List<IoCompletionListener1<Boolean>> ios) {

			if (isDeviceBusy()) {
				cmdCache.enqueRequest(opCode, floors, ios);
				//				cmdCache.dumpLast();
				// if (node != null) {
				// // Log.i(LongLakeApplication.DANIEL_TAG ,"I am busy , guy");
				// printArray("I am busy,guy" + LiftOpCodeName[node.getCode()],
				// node.getOpFloorSet());
				// }
			} else {
				bt.setDeviceBusy(true);
				runInMagicThread(r);
			}
		}

	}

	private static class LLLiftCmdCache extends LinkedList<LLLiftCmdCacheNode> {

		private static final long serialVersionUID = -2241140103322253716L;

		public void enqueRequest(int code, String bitMap, List<IoCompletionListener1<Boolean>> ios) {
			LLLiftCmdCacheNode newNode = new LLLiftCmdCacheNode(code, bitMap, ios);
			LLLiftCmdCacheNode lastOne = this.peekLast();
			if (lastOne == null) {
				this.addLast(newNode);
			} else if (!lastOne.combinedRequest(newNode)) {
				this.addLast(newNode);
			}
		}

		public void enqueRequest(int code, List<Integer> bitMap, List<IoCompletionListener1<Boolean>> ios) {

			LLLiftCmdCacheNode newNode = new LLLiftCmdCacheNode(code, bitMap, ios);
			LLLiftCmdCacheNode lastOne = this.peekLast();

			if (lastOne == null) {
				this.addLast(newNode);
			} else if (!lastOne.combinedRequest(newNode)) {
				this.addLast(newNode);
			}
		}

		public LLLiftCmdCacheNode dumpLast() {
			return this.getLast();
		}

		public LLLiftCmdCacheNode dequeueRequest() {
			try {
				return removeFirst();
			} catch (Exception e) {
				return null;
			}
		}
	}

	final BusyTag bt = new BusyTag();

	private void invokeIoCompletion(Boolean data, Object context, final List<IoCompletionListener1<Boolean>> ios) {
		for (IoCompletionListener1<Boolean> _io : ios) {
			_io.onFinish(data, context);
		}
	}

	IoCompletionListener1<Boolean> _default_io = new IoCompletionListener1<Boolean>() {

		@Override
		public void onFinish(Boolean data, Object context) {
			// bt.setDeviceBusy(false);
			// _redispatchRequest();
		}
	};

	List<IoCompletionListener1<Boolean>> _default_ios = new ArrayList<IoCompletionListener1<Boolean>>();

	final LLLiftCmdCache cmdCache = new LLLiftCmdCache();

	private void assertThreadContext() {
		LLSDKUtils.danielAssert(Looper.myLooper().equals(mLiftThreadHandler.getLooper()));
	}

	private void runInMagicThread(final Runnable r) {
		if (mLiftThreadHandler.getLooper().equals(Looper.myLooper())) {
			r.run();
		} else {
			mLiftThreadHandler.post(r);
		}
	}

	public void setAllowFloors(String bitmap, final List<IoCompletionListener1<Boolean>> ios) {
		_executeRequest(new LLLiftCmdCacheNode(opAllowCode, bitmap, ios == null ? _default_ios : ios));
	}

	public void setAllFloorsAllow(String bitmap, final List<IoCompletionListener1<Boolean>> ios) {
		_executeRequest(new LLLiftCmdCacheNode(opAllAllowCode, bitmap, ios == null ? _default_ios : ios));
	}

	public void setAllFloorsDisallow(String bitmap, final List<IoCompletionListener1<Boolean>> ios) {
		_executeRequest(new LLLiftCmdCacheNode(opAllDisableCode, bitmap, ios == null ? _default_ios : ios));
	}

	private interface IConfigureCallback {
		public void invoke(final LLLiftCmdCacheNode node);
	}

	private IConfigureCallback configureCallbacks[] = { new IConfigureCallback() {

		@Override
		public void invoke(LLLiftCmdCacheNode node) {
			throw new RuntimeException("no initialization");
		}

	}, new IConfigureCallback() {

		@Override
		public void invoke(LLLiftCmdCacheNode node) {
			_setAllowFloors(node.getOpFloorSet(), node.getOpIoList());
		}

	}, new IConfigureCallback() {

		@Override
		public void invoke(LLLiftCmdCacheNode node) {
			_disableFloorRequest(node.getOpFloorSet(), node.getOpIoList());
		}

	}, new IConfigureCallback() {

		@Override
		public void invoke(LLLiftCmdCacheNode node) {
			_setDirectFloor(node.getOpFloorSet(), node.getOpIoList());
		}

	}, new IConfigureCallback() {

		@Override
		public void invoke(LLLiftCmdCacheNode node) {
			_setDisableDirectFloor(node.getOpFloorSet(), node.getOpIoList());
		}

	}, new IConfigureCallback() {

		@Override
		public void invoke(LLLiftCmdCacheNode node) {
			_setAllFloorsAllow(node.getOpFloorSet(), node.getOpIoList());
		}

	}, new IConfigureCallback() {

		@Override
		public void invoke(LLLiftCmdCacheNode node) {
			_setAllFloorDisallow(node.getOpFloorSet(), node.getOpIoList());
		}

	}, };

	private void _executeRequest(final LLLiftCmdCacheNode node) {
		if (node.getOpFloorSet().size() == 0) {
			runInMagicThread(new Runnable() {
				public void run() {
					invokeIoCompletion(true, null, node.getOpIoList());
				}
			});
			return;
		}
		try {
			printArray("_executeRequest " + LiftOpCodeName[node.getCode()], node.getOpFloorSet());
			configureCallbacks[node.getCode()].invoke(node);
		} catch (Exception e) {
			Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
		}
	}

	private void _redispatchRequest() {

		LLLiftCmdCacheNode node = cmdCache.dequeueRequest();
		if (node != null) {
			_executeRequest(node);
		}
		//Log.i(LongLakeApplication.DANIEL_TAG, "_redispatchRequest " + Boolean.valueOf(node != null).toString());
	}

	public boolean isWorking() {
		return LLSDKUtils.checkBit(busyState, ELEVATOR_BUSY);
	}

	public boolean isDisabled() {
		return LLSDKUtils.checkBit(busyState, DISABLE_ALL);
	}

	public boolean isEnabled() {
		return LLSDKUtils.checkBit(busyState, ENABLE_ALL);
	}

	private void _setAllFloorDisallow(final List<Integer> disabledFloors,
			final List<IoCompletionListener1<Boolean>> ios) {
		boolean ret = false;
		Log.e(LongLakeApplication.DANIEL_TAG, "busystate " + busyState);
		synchronized (this) {
			if (busyState == INIT_STATE || LLSDKUtils.checkBit(busyState, DISABLE_ALL | ELEVATOR_BUSY)) {
				ret = true;
			} else {
				busyState = DISABLE_ALL | ELEVATOR_BUSY;
			}
		}

		if (ret) {
			invokeIoCompletion(true, null, ios);
			return;
		}

		bt.testBusyBitAndRun(opAllDisableCode, new Runnable() {
			public void run() {
				byte[] f2 = makeAllowDisableFloorBitmap(disabledFloors);
				_sendRequestWithTries(f2, disabledFloors, new IoCompletionListener1<Boolean>() {
					@Override
					public void onFinish(Boolean ret, Object context) {
						synchronized (this) {
							busyState = (ret ? DISABLE_ALL : ENABLE_ALL);
						}

						if (!ret) {
							dumpCmdString("_setAllFloorDisallow failed", allowFloorState);
							makeAllowEnableFloorBitmap(disabledFloors);
						}
						bt.setDeviceBusy(false);
						invokeIoCompletion(ret, context, ios);
						_redispatchRequest();
					}
				});
			}
		}, disabledFloors, ios);
	}

	private void _setAllFloorsAllow(final List<Integer> floors, final List<IoCompletionListener1<Boolean>> ios) {

		boolean ret = false;
		synchronized (this) {
			if (LLSDKUtils.checkBit(busyState, ENABLE_ALL | ELEVATOR_BUSY)) {
				ret = true;
			} else {
				busyState = ENABLE_ALL | ELEVATOR_BUSY;
			}
		}
		if (ret) {
			invokeIoCompletion(true, null, ios);
			return;
		}
		runInMagicThread(new Runnable() {
			@Override
			public void run() {
				bt.testBusyBitAndRun(opAllAllowCode, new Runnable() {
					public void run() {
						byte[] cmd = makeAllowEnableFloorBitmap(floors);
						_sendRequestWithTries(cmd, floors, new IoCompletionListener1<Boolean>() {
							@Override
							public void onFinish(Boolean ret, Object context) {
								synchronized (this) {
									busyState = (ret ? ENABLE_ALL : DISABLE_ALL);
								}
								if (!ret) {
									dumpCmdString("setAllFloorsAllow failed", allowFloorState);
									makeAllowDisableFloorBitmap(floors);
								}
								bt.setDeviceBusy(false);
								invokeIoCompletion(ret, context, ios);
								_redispatchRequest();
							}
						});
					}
				}, floors, ios);
			}
		});
	}

	private void _setAllowFloors(final List<Integer> floors, final List<IoCompletionListener1<Boolean>> ios) {

		runInMagicThread(new Runnable() {
			@Override
			public void run() {
				bt.testBusyBitAndRun(opAllowCode, new Runnable() {
					public void run() {
						byte[] cmd = makeAllowEnableFloorBitmap(floors);
						// post a delayed runnable with 1min delayed to disble
						// just
						// dispatched
						// floor request
						_sendRequestWithTries(cmd, floors, new IoCompletionListener1<Boolean>() {
							@Override
							public void onFinish(Boolean ret, Object context) {
								bt.setDeviceBusy(false);
								invokeIoCompletion(ret, context, ios);
								if (!ret) {
									dumpCmdString("_setAllowFloors failed", allowFloorState);
								} else {
									ArrayList<Integer> list = new ArrayList<Integer>();
									for (Integer floor : floors) {
										list.add(elevatorData.toRealFloor(floor));
									}
									LLElevatorController.getInstance().notifyOb(new Object[] { list, opAllAllowCode });
								}
								mLiftThreadHandler.postDelayed(new Runnable() {
									@Override
									public void run() {
										_executeRequest(
												LLLiftCmdCacheNode.makeDisallowRequestNode(floors, _default_ios));
									}
								}, LLSdkGlobals.getElevator_allowexpire_duration());

							}
						});
					}
				}, floors, ios);
			}
		});
	}

	private void _disableFloorRequest(final List<Integer> disabledFloors,
			final List<IoCompletionListener1<Boolean>> ios) {
		assertThreadContext();
		bt.testBusyBitAndRun(opDisallowCode, new Runnable() {
			public void run() {
				byte[] f2 = makeAllowDisableFloorBitmap(disabledFloors);
				_sendRequestWithTries(f2, disabledFloors, new IoCompletionListener1<Boolean>() {
					@Override
					public void onFinish(Boolean ret, Object context) {
						bt.setDeviceBusy(false);
						invokeIoCompletion(ret, context, ios);
						_redispatchRequest();
					}
				});
			}
		}, disabledFloors, ios);
	}

	private void _sendRequestWithTries(final byte[] cmd, final List<Integer> opFloors,
			final IoCompletionListener1<Boolean> io) {

		final LLCursorWithMaximum counter = new LLCursorWithMaximum(3);
		SerialPosClient.instance().sendDataViaCom(cmd, new IoCompletionListener1<byte[]>() {
			@Override
			public void onFinish(byte[] data, Object context) {
				final CmdErrorCode errorCode = (CmdErrorCode) context;

				if (!errorCode.needToResend()) {
					runInMagicThread(new Runnable() {
						public void run() {
							io.onFinish(errorCode.noError(), null);
						}
					});
				} else {
					counter.moveToNext();
					if (counter.hitMax()) {
						runInMagicThread(new Runnable() {
							public void run() {
								io.onFinish(false, null);
							}
						});
					} else {
						//						Log.i(LongLakeApplication.DANIEL_TAG, "try " + counter.getPosition());
						SerialPosClient.instance().sendDataViaCom(cmd, this);
					}

				}
			}
		});
	}

	public void setDirectFloor(final Integer[] floors, List<IoCompletionListener1<Boolean>> ios) {

		if (ios == null) {
			ios = _default_ios;
		}

		if (floors.length == 0) {
			invokeIoCompletion(true, null, ios);
		} else {
			_setDirectFloor(Arrays.asList(floors), ios);
		}

	}

	public void setDirectFloor(final String floorBitmap, final List<IoCompletionListener1<Boolean>> ios) {
		_executeRequest(new LLLiftCmdCacheNode(opDirectCode, floorBitmap, ios == null ? _default_ios : ios));
	}

	private void _setDirectFloor(final List<Integer> floors, final List<IoCompletionListener1<Boolean>> ios) {
		runInMagicThread(new Runnable() {
			@Override
			public void run() {
				bt.testBusyBitAndRun(opDirectCode, new Runnable() {
					public void run() {
						byte[] cmd = makeDirectEnableFloorBitmap(floors);
						_sendRequestWithTries(cmd, floors, new IoCompletionListener1<Boolean>() {
							@Override
							public void onFinish(Boolean ret, Object context) {
								bt.setDeviceBusy(false);
								invokeIoCompletion(ret, context, ios);

								if (!ret) {
									dumpCmdString("_setDirectFloor failed", allowFloorState);
								} else {
									ArrayList<Integer> list = new ArrayList<Integer>();
									for (Integer floor : floors) {
										list.add(elevatorData.toRealFloor(floor));
									}
									LLElevatorController.getInstance().notifyOb(new Object[] { list, opDirectCode });
								}

								mLiftThreadHandler.postDelayed(new Runnable() {
									@Override
									public void run() {
										_executeRequest(
												LLLiftCmdCacheNode.makeDisableDirectRequestNode(floors, _default_ios));
									}
								}, LLSdkGlobals.getElevator_directexpire_duration());

							}
						});
					}
				}, floors, ios);
			}

		});
	}

	private void _setDisableDirectFloor(final List<Integer> floors, final List<IoCompletionListener1<Boolean>> ios) {
		assertThreadContext();
		bt.testBusyBitAndRun(opDiscardDirectCode, new Runnable() {
			public void run() {
				byte[] f2 = makeDirectDisableFloorBitmap(floors);
				_sendRequestWithTries(f2, floors, new IoCompletionListener1<Boolean>() {
					@Override
					public void onFinish(Boolean ret, Object context) {
						bt.setDeviceBusy(false);
						invokeIoCompletion(ret, context, ios);
						if (!ret) {
							dumpCmdString("_setDisableDirectFloor failed", directFloorState);
						}
						_redispatchRequest();
					}
				});
			}
		}, floors, _default_ios);
	}

	private void alwaysEnableDefaultAllowFloor(byte[] floorBitmap) {
		LLElevatorData elevatorData = getElevatorData();
		try {
			int firstFloor = elevatorData.getFirstFloor();
			for (int floor : getAllowFloors(elevatorData.getAllowFloors())) {
				int setFloor = 0;
				setFloor = floor - firstFloor + (LLSDKUtils.oppositeSigns(floor, firstFloor) ? 0 : 1);
				int t = (setFloor - 1) >> 3;
				int result = (1 << ((setFloor + 7) & 7));
				floorBitmap[floorBitmap.length - 1 - t] = (byte) (result | floorBitmap[floorBitmap.length - 1 - t]);
			}
		} catch (Exception e) {
			Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
		}
	}

	private int[] getAllowFloors(String str) {
		String[] arr = str.split(",");
		int[] floors = new int[arr.length];
		for (int i = 0; i < floors.length; i++) {
			floors[i] = Integer.parseInt(arr[i]);
		}
		return floors;
	}

	private byte[] makeAllowDisableFloorBitmap(List<Integer> disabledFloors) {
		_makeDisableFloorBitmap(disabledFloors, allowReferenceCount, allowFloorState);
		alwaysEnableDefaultAllowFloor(allowFloorState);
		return allowFloorState;
	}

	private byte[] makeAllowEnableFloorBitmap(List<Integer> allowedFloors) {
		_makeEnableFloorBitmap(allowedFloors, allowReferenceCount, allowFloorState);
		alwaysEnableDefaultAllowFloor(allowFloorState);
		return allowFloorState;

	}

	private byte[] makeDirectEnableFloorBitmap(List<Integer> directFloor) {
		// _makeEnableFloorBitmap(directFloor, allowReferenceCount,
		// allowFloorState);
		_makeEnableFloorBitmap(directFloor, directReferenceCount, directFloorState);
		return directFloorState;
	}

	private byte[] makeDirectDisableFloorBitmap(List<Integer> directFloor) {
		_makeDisableFloorBitmap(directFloor, directReferenceCount, directFloorState);
		return directFloorState;
	}

	public void dumpCmdString(String prefix, byte[] b) {
		Log.d(LongLakeApplication.DANIEL_TAG, prefix);
	}

	private void _makeEnableFloorBitmap(List<Integer> FloorsList, int[] referenceCount, byte[] floorBitmap) {

		for (int enabledFloor : FloorsList) {
			if (referenceCount[enabledFloor - 1] == 0) {
				int result = 1 << ((enabledFloor + 7) & 7);
				int t = (enabledFloor - 1) >> 3;
				floorBitmap[floorBitmap.length - 1 - t] = (byte) (result | floorBitmap[floorBitmap.length - 1 - t]);

			}
			referenceCount[enabledFloor - 1]++;
		}
	}

	private void _makeDisableFloorBitmap(List<Integer> FloorsList, int[] referenceCount, byte[] floorBitmap) {

		for (int reallyNode : FloorsList) {
			if (referenceCount[reallyNode - 1] == 1) {
				int t = (reallyNode - 1) >> 3;
		int result = ~(1 << ((reallyNode + 7) & 7));
		floorBitmap[floorBitmap.length - 1 - t] = (byte) (result & floorBitmap[floorBitmap.length - 1 - t]);

			}
			referenceCount[reallyNode - 1]--;
		}
	}

	public LLElevatorData getElevatorData() {
		return elevatorData;
	}

	public void setElevatorBoard(int boardNum) {
		elevatorData.setBoardNum(boardNum);
	}
	public void setElevatorState(boolean state) {
		elevatorData.setElevatormanaged(state);
		save();
	}

	public void setElevatorInfo(String elevatorInfo) {
		elevatorData.setElevatorInfo(elevatorInfo);
	}

	public void setSchedulerDeviceLocation(String schedulerDeviceLocation) {
		elevatorData.setSchedulerDeviceLocation(schedulerDeviceLocation);
	}

	public void setElevatorMaxFloor(int count) {
		elevatorData.setMaxFloor(count);
	}

	public void setElevatorFirstFloor(int firstFloor) {
		elevatorData.setFirstFloor(firstFloor);
	}

	public void setElevatorAllowFloors(String floors) {
		elevatorData.setAllowFloors(floors);
	}

	public void setElevatorParentId(String parentId) {
		elevatorData.setParentId(parentId);
	}

	public Collection<String> getElevatorArray() {
		return elevatorData.getElevatorInfo() ; 
	}

	public int getLocation() {
		try {
			if (!TextUtils.isEmpty(elevatorData.getSchedulerDeviceLocation())) {
				return Integer.parseInt(elevatorData.getSchedulerDeviceLocation());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 1;
	}

	public void setElevatorData(String ver, String schedulerDeviceLocation, String elevatorInfo) {
		setElevatorInfo(elevatorInfo);
		setSchedulerDeviceLocation(schedulerDeviceLocation);
		save(ver);
	}

	private List<String> manipulatedElevators = new ArrayList<String>() ; 
	public void setElevatorData(boolean managered, String ver, int boardNum, int firstFloor, int count, String allowFloors,
			String parentId) {
		elevatorData.setElevatormanaged(managered);
		setElevatorBoard(boardNum);
		setElevatorFirstFloor(firstFloor);
		setElevatorMaxFloor(count);
		setElevatorAllowFloors(allowFloors);
		setElevatorParentId(parentId);
		save(ver);
	}

	private int direction = UP_STATUS;
	private int logicalFloor = INIT_LOGICAL_FLOOR;
	private int realFloor;

	public static int calcDirection(final int start, final int end) {
		return start - end > 0 ? DOWN_STATUS : UP_STATUS;
	}

	public void setElevatorRunningStatus(int logicalFloor) {
//		android.util.Log.i(LongLakeApplication.DANIEL_TAG , "setElevatorRunningStatus " + arrow_str[direction] + " " + logicalFloor);

//		if (this.direction != direction) {
//			this.direction = direction;
//			directionNotifier.notifyOb(new Object[] { direction });
//		}

		if (this.logicalFloor != logicalFloor) {
			int _direction = calcDirection(this.logicalFloor , logicalFloor);
			this.logicalFloor = logicalFloor;
			if ( _direction != direction ) {
				direction = _direction ;
				directionNotifier.notifyOb(new Object[] { direction });
			}
			this.realFloor = elevatorData.toRealFloor(logicalFloor);
			floorNotifier.notifyOb(new Object[] { realFloor, logicalFloor });
		}

	}

	public int getRealFloor() {
		return realFloor;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	private void save(String ver) {
		this.version = ver;
		String strJson = LLGsonUtils.getInstance().toJson(elevatorData);
		String str = LLGsonUtils.getInstance().toJson(new LLInfoDetails(ver, strJson));
		LLCacheUtils.writeToFileEx(ElevatorInfoFileName, str, LongLakeApplication.getInstance());
		android.util.Log.i(LongLakeApplication.DANIEL_TAG, "elevatorjson:" + str);
	}

	private void save() {
		save(version);
	}

	private LLNotifier<IElevatorDirectionListener> directionNotifier = new LLNotifier<IElevatorDirectionListener>() {

		@Override
		protected void invoke(IElevatorDirectionListener t, Object[] o1) {
			t.onElevatorDirectionChanged((int) o1[0]);
		}

		@Override
		protected boolean dispatchedInMainThread() {
			return true;
		}
	};

	public LLNotifier<IElevatorDirectionListener> getDirectionNotifier() {
		return directionNotifier;
	}

	private LLNotifier<IElevatorFloorListener> floorNotifier = new LLNotifier<IElevatorFloorListener>() {

		@Override
		protected void invoke(IElevatorFloorListener t, Object[] o1) {
			t.onElevatorFloorChanged((int) o1[0], (int) o1[1]);
		}

		@Override
		protected boolean dispatchedInMainThread() {
			return true;
		}
	};

	public LLNotifier<IElevatorFloorListener> getFloorNotifier() {
		return floorNotifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void invoke(ISpeechFloorVoice t, Object[] o1) {
		t.onSpeechFloor((List<Integer>) o1[0], (int) o1[1]);
	}

}
