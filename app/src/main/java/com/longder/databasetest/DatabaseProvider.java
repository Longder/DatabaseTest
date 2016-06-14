package com.longder.databasetest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Longder on 2016/6/14.
 */
public class DatabaseProvider extends ContentProvider {
    /**
     * 声明四个常量用来匹配不同的uri
     */
    private static final int BOOK_DIR = 0;
    private static final int BOOK_ITEM = 1;
    private static final int CATEGORY_DIR = 2;
    private static final int CATEGORY_ITEM = 3;

    /**
     * 按照惯例定义的访问本系统数据的权限名
     */
    private static final String AUTHORITY = "com.longder.databasetest.provider";

    /**
     * uri匹配器
     */
    private static UriMatcher uriMatcher;
    /**
     * SQLite帮助类
     */
    private SQLiteOpenHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "book", BOOK_DIR);
        uriMatcher.addURI(AUTHORITY, "book/#", BOOK_ITEM);
        uriMatcher.addURI(AUTHORITY, "category", CATEGORY_DIR);
        uriMatcher.addURI(AUTHORITY, "category/#", CATEGORY_ITEM);
    }

    @Override
    public boolean onCreate() {
        //创建dbHelper实例，指定数据库名和数据库版本号
        dbHelper = new MyDatabaseHelper(getContext(), "BookStore.db", null, 2);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //获取只读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        //通过uri匹配判断用户要执行的操作
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR: //查询Book表全部
                cursor = db.query("Book", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ITEM://根据id查找Book表某条记录
                /**
                 * getPathSegments方法是获取uri的path部分，并且去掉了分隔符"/"，返回的是一个集合。
                 *比如：形如"book/#"的path，get(0)得到book，get(1)得到#
                 */
                String bookId = uri.getPathSegments().get(1);
                cursor = db.query("Book", projection, "id=?", new String[]{bookId}, null, null, sortOrder);
                break;
            case CATEGORY_DIR://查询Category表全部
                cursor = db.query("Category", projection, null, null, null, null, sortOrder);
                break;
            case CATEGORY_ITEM://根据id查找Category表某条记录
                String categoryId = uri.getPathSegments().get(1);
                cursor = db.query("Category", projection, "id=?", new String[]{categoryId}, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                return "vnd.android.cursor.dir/vnd.com.longder.databasetest.provider.book";
            case BOOK_ITEM:
                return "vnd.android.cursor.item/vnd.com.longder.databasetest.provider.book";
            case CATEGORY_DIR:
                return "vnd.android.cursor.dir/vnd.com.longder.databasetest.provider.category";
            case CATEGORY_ITEM:
                return "vnd.android.cursor.item/vnd.com.longder.databasetest.provider.category";
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //获取只读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
            case BOOK_ITEM://只要是对Book表的插入都执行以下代码
                long newBookId = db.insert("Book", null, values);
                //将插入之后返回的新数据的id值封装到uri中返回
                uriReturn = Uri.parse("content://" + AUTHORITY + "/book/" + newBookId);
                break;
            case CATEGORY_DIR:
            case CATEGORY_ITEM:
                long newCategoryId = db.insert("Category", null, values);
                //将插入之后返回的新数据的id值封装到uri中返回
                uriReturn = Uri.parse("content://" + AUTHORITY + "/category/" + newCategoryId);
                break;
        }
        return uriReturn;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //获取只读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //删除的行数
        int deleteRows = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK_ITEM:
                deleteRows = db.delete("Book", selection, selectionArgs);
                break;
            case BOOK_DIR:
                String bookId = uri.getPathSegments().get(1);
                deleteRows = db.delete("Book", "id=?", new String[]{bookId});
                break;
            case CATEGORY_DIR:
                deleteRows = db.delete("Category", selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                deleteRows = db.delete("Category", "id=?", new String[]{categoryId});
                break;
        }
        return deleteRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //获取只读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //更新的行数
        int updateRows = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                updateRows = db.update("Book", values, selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                updateRows = db.update("Book", values, "id=?", new String[]{bookId});
                break;
            case CATEGORY_DIR:
                updateRows = db.update("Category", values, selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                updateRows = db.update("Category", values, "id=?", new String[]{categoryId});
                break;
        }
        return updateRows;
    }
}
