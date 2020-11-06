package com.xh.mian.myapp.tools.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xh.mian.myapp.MyApplication;
import com.xh.mian.myapp.tools.uitl.SDCardHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper extends SQLiteOpenHelper {

	private static final String EBTRY_NAME = "my.db";
//	private static final String EBTRY_NAME =Environment.getExternalStorageDirectory().toString()
//					+"/乳腺肿瘤entry2019-09-05";
	private static final int EBTRY_VERSION=2;
	private SQLiteDatabase mDatabase = null;
	private Cursor cursor = null;

	// 基本信息
	private final String USER_TABLE = "create table user_table(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"username TEXT,Password TEXT)";
	// 操作日志
	public static final String LOG_TABLE = "create table logv(id INTEGER PRIMARY KEY AUTOINCREMENT, log_date TEXT,users TEXT,server_id TEXT,base_id TEXT,patient_name TEXT,table_name TEXT,table_id TEXT,describe TEXT,details TEXT)";
	public static final String TABLE_NAME_LOG = "logv";

	public DbHelper(Context context) {
		super(context, EBTRY_NAME, null, EBTRY_VERSION);
		mDatabase = setAssetsDb(context);
		//mDatabase = SQLiteDatabase.openOrCreateDatabase(EBTRY_NAME, null);
	}
	private SQLiteDatabase setAssetsDb(Context context) {
		String filePath = SDCardHelper.getSDCardPrivateFilesDir(context, null)+"/my.db";
		File jhPath=new File(filePath);
		if(jhPath.exists()){
			if(!MyApplication.instance.isdbk){
				MyApplication.instance.isdbk = true;
				jhPath.delete();
			}else {
				return SQLiteDatabase.openOrCreateDatabase(filePath, null);
			}
		}
		try {
			//得到资源
			AssetManager am= context.getAssets();
			//得到数据库的输入流
			InputStream is=am.open("my.db");
			//用输出流写到SDcard上面
			FileOutputStream fos=new FileOutputStream(jhPath);
			//创建byte数组  用于1KB写一次
			byte[] buffer=new byte[1024];
			int count = 0;
			while((count = is.read(buffer))>0){
				fos.write(buffer,0,count);
			}
			//最后关闭就可以了
			fos.flush();
			fos.close();
			is.close();
			return SQLiteDatabase.openOrCreateDatabase(filePath, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getWritableDatabase();
	}
	// 建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.mDatabase = db;
		// 基本信息
		db.execSQL(USER_TABLE);
	}
	
	/**
	 * 更新表
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < 2)
		{
			if (!isColumnExist(db,"tableName", "name")) {
				db.execSQL("alter table tableName add column name TEXT");
			}

		}
	}

	// 关闭数据库
	public void close() {
		if (mDatabase != null) {
			mDatabase.close();
			mDatabase = null;
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	//判断字段是否存在表里面
	public synchronized boolean isColumnExist(SQLiteDatabase db,
                                              String tableName, String colName) {
		List<String> colList = getColNames(db, tableName);
		if (colList.contains(colName))
			return true;
		return false;
	}
	//获取表所有字段
	private synchronized List<String> getColNames(SQLiteDatabase db,
                                                  String tableName) {
		List<String> colNames = new ArrayList<String>();
		try {
			Cursor cursor = db.query(tableName, null, "1=0", null, null,
					null, null);
			int count = cursor.getColumnCount();
			for (int i = 0; i < count; i++) {
				colNames.add(cursor.getColumnName(i));
			}

			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return colNames;
	}
	public synchronized List<String> getColNames(String tableName) {

		List<String> colNames = new ArrayList<String>();
		try {
			Cursor cursor = mDatabase.query(tableName, null, "1=0", null, null,
					null, null);
			int count = cursor.getColumnCount();
			for (int i = 0; i < count; i++) {
				colNames.add(cursor.getColumnName(i));
			}

			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return colNames;
	}
	/**
	 * 插入表数据
	 * @param values 插入内容
	 * @param TABLE1  表名字
	 * @return
	 */
	public long insert(String TABLE1, ContentValues values) {
		long len = -1L;
		len = mDatabase.insert(TABLE1, null, values);
		return len;
	}

    //删除表
	public void delete(String TABLE1) {
		mDatabase.delete(TABLE1, null, null);
	}
	//根据条件删除表
	public int deleteByWhere(String tableName, String where, String[] whereArgs) {
		int r = mDatabase.delete(tableName,where,whereArgs);
		return r;
	}
	//sql语句
	private void revertSeq(String FEED_TABLE_NAME) {
	    String sql = "update sqlite_sequence set seq=0 where name='"+FEED_TABLE_NAME+"'";
		mDatabase.execSQL(sql);
	}
	
	/**
	 * 根据主键id 跟新
	 * @param values 字段内容
	 * @param TABLE1 表名
	 */
	public void update(String TABLE1, ContentValues values, String where, String[] args) {
		mDatabase.update(TABLE1, values, where, args);
	}

	//
	public synchronized Map<String, Object> getOneByUnique(
            String tableName, String where, String[] args, String orderBy) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			cursor = mDatabase.query(tableName, null,where, args, null, null, orderBy);
			int count = cursor.getColumnCount();
			if (cursor.moveToFirst()) {
				for (int i = 0; i < count; i++) {
					map.put(cursor.getColumnName(i), cursor.getString(i));
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/***
	 * 
	 * @param where  条件
	 * @param args   条件对应标签
	 * @param tableName 表名
	 * @param orderBy  排序
	 * @return
	 */
	public synchronized List<Map<String, Object>> getListByTableName(
            String tableName, String where, String[] args, String orderBy) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			cursor = mDatabase.query(tableName, null,
					where, args, null, null, orderBy);
			int count = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < count; i++) {
					map.put(cursor.getColumnName(i), cursor.getString(i));
				}
				data.add(map);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return data;
	}



}
