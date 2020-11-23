package com.xixi.sdk.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.util.LruCache;
import android.util.Log;

public class LLCacheUtils extends SQLiteOpenHelper {

	private final static String TAG = LongLakeApplication.DANIEL_TAG;
	private HandlerThread ht;
	private Handler taskThread;
	private LruCache<String, String> mMemoryCache;

	private static final String DATABASE_NAME = "kv.db";
	protected static final int DATABASE_VERSION = 1;

	private static LLCacheUtils instance;

	public static synchronized LLCacheUtils getInstance(Context context) {
		if (instance == null) {
			instance = new LLCacheUtils(context);
		}
		return instance;
	}

	Context context;

	private void runTask(final Runnable r) {
		taskThread.post(r);
	}

	private void _runSql(String sql) {
		_runSql(sql, null);
	}

	private void _runSql(String sql, final Runnable r) {
		try {
//			Log.d(TAG, "sql=>" + sql);
			db.execSQL(sql);
		} catch (SQLiteException e) {

			if (r != null)
				r.run();
		}
	}

	public void clean() {
		runTask(new Runnable() {
			public void run() {
				_runSql("delete from kv_table");

			}
		});

		synchronized (mMemoryCache) {
			mMemoryCache.evictAll() ;
		}
	}

	public static synchronized LLCacheUtils getInstance() {
		return instance;
	}

	protected LLCacheUtils(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		final int memClass = ((ActivityManager) LLSDKUtils.getContext().getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass();

		ht = new HandlerThread("fileCache dispatch thread");
		ht.start();
		taskThread = new Handler(ht.getLooper());

		final int cacheSize = (1 << 20); // 1M
		mMemoryCache = new LruCache<String, String>(cacheSize) {
			@Override
			protected int sizeOf(String key, String content) {
				// The cache size will be measured in bytes rather than number
				// of items.
				return content.length();
			}
		};

		db = getWritableDatabase();
	}

	private final static String kv_table_name = "kv_table";
	private SQLiteDatabase db;

	private void _rvFile(final String K) {

		runTask(new Runnable() {
			public void run() {
				_runSql(String.format("delete from kv_table where key_in_map ='%s' ", K));

			}
		});

		synchronized (mMemoryCache) {
			mMemoryCache.remove(K);
		}
	}

	public void rvFile(final String fileName) {
		_rvFile(fileName);
	}

	private void _write(final String K, final String V, final Context context) {
		LLSDKUtils.danielAssert(LLSDKUtils.isInMainThread());
		runTask(new Runnable() {
			public void run() {

				_runSql(String.format("insert into kv_table (key_in_map , value_in_map) values('%s' , '%s')", K, V),
						new Runnable() {
							public void run() {
								_runSql(String.format("update kv_table set value_in_map='%s' where key_in_map='%s'", V,
										K));
							}
						});
			}
		});

		synchronized (mMemoryCache) {
			mMemoryCache.put(K, V);
		}
	}

	private void _read(final String K, final Context context, final IoCompletionListener1<String> readyCallback) {
		LLSDKUtils.danielAssert(LLSDKUtils.isInMainThread());

		String data;
		synchronized (mMemoryCache) {
			data = mMemoryCache.get(K);
		}

		if (data == null) {
			this.runTask(new Runnable() {
				public void run() {
					String result = "";
					final String s = String.format("select value_in_map from kv_table where key_in_map ='%s' ", K);
					Cursor cursor = null;
					try {
						cursor = db.rawQuery(s, null);
						if (cursor.getCount() != 0) {
							cursor.moveToFirst();
							result = cursor.getString(0);
							synchronized (mMemoryCache) {
								mMemoryCache.put(K, result);
							}
						}
					} catch (Exception e) {
						Log.e(TAG, Log.getStackTraceString(e));
					}

					if (cursor != null) {
						try {
							cursor.close();
						} catch (Exception e) {
						}
					}

					final String vacant = result;
					UIThreadDispatcher.dispatch(new Runnable() {
						public void run() {
							readyCallback.onFinish(vacant, context);
						}
					});
				}
			});
		} else {

			if (LLSDKUtils.isInMainThread()) {
				readyCallback.onFinish(data , context );
			} else {
				final String _data = data;
				UIThreadDispatcher.dispatch(new Runnable() {

					public void run() {
						readyCallback.onFinish(_data, context);
					}

				});
			}

		}
	}

	public void writeToFile(String fileName, Object data) {
		_write(fileName, (String) data, null);
	}

	public void writeToFile(String fileName, Object data, Context context) {
		_write(fileName, (String) data, context);
	}

	public void readFromFile(String strFilename, IoCompletionListener1<String> readyCallback) {
		_read(strFilename, null, readyCallback);
	}

	public void readFromFile(String strFilename, Context context, IoCompletionListener<String> readyCallback) {
		_read(strFilename, context, readyCallback);
	}

	public static void writeToFileEx(String strFilename, String data, Context context) {
		OutputStreamWriter os = null;
		try {
			os = new OutputStreamWriter(context.openFileOutput(strFilename, Context.MODE_PRIVATE));
			os.write(data);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			if (os != null) {
				try {
					os.close();
					os = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static String readFromFileEx(String strFilename, Context context) {
		InputStream inputStream = null;
		try {
				inputStream = context.openFileInput(strFilename);

			if (inputStream != null) {

				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				inputStream = null;
				return stringBuilder.toString();
			}
		} catch (Exception e) {
			Log.i(LongLakeApplication.DANIEL_TAG,Log.getStackTraceString(e));
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
			}
		}

		return "";
	}

	private final static String create_kv_table = "create table if not exists kv_table( key_in_map char(100) primary key not null ,value_in_map text not null )";
	private final static String drop_kv_sql = "drop table if exists kv_table";

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(create_kv_table);
		db = arg0;
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		arg0.execSQL(drop_kv_sql);
		arg0.execSQL(create_kv_table);
		db = arg0;
	}
}
