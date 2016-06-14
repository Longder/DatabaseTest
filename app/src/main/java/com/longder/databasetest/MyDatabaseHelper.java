package com.longder.databasetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * 自定义的数据库帮助类，继承于系统提供的SQLiteOpenHelper
 * Created by Longder on 2016/5/30.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    /**
     * 创建BOOK表的SQL语句
     */
    private static final String CREATE_BOOK = "create table Book ("
            + "id integer primary key autoincrement, "
            + "author text, "
            + "price real, "
            + "pages integer, "
            + "name text)";
    /**
     * 创建CATEGORY表的SQL语句
     */
    private static final String CREATE_CATEGORY = "create table Category("
            + "id integer primary key autoincrement, "
            + "category_name text, "
            + "category_code integer)";
    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 构造方法中调用父类构造方法
     *
     * @param context
     * @param name    数据库名
     * @param factory
     * @param version 数据库版本号
     */
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    /**
     * 数据库被创建时执行的方法
     *
     * @param db 被创建好的数据库对象
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //执行SQL语句
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CATEGORY);
        Toast.makeText(mContext, "创建成功", Toast.LENGTH_LONG).show();
    }

    /**
     * 系统发现数据库需要更新时调用的方法
     *
     * @param db
     * @param oldVersion 会传递进来原数据库的版本号
     * @param newVersion 现在新数据库的版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                db.execSQL(CREATE_CATEGORY);
                break;
            default:

        }
    }
}
