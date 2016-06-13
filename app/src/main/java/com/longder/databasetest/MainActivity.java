package com.longder.databasetest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建一个SQLiteOpenHelper对象
        dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 2);
        /**
         * 建表测试
         */
        Button createButton = (Button) findViewById(R.id.create_database);
        assert createButton != null;
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 获取一个可写数据库，此时会调用onCreate方法
                 * onCreate方法只会调用一次，在数据库被创建之后就不会再调用
                 */
                dbHelper.getWritableDatabase();
            }
        });
        /**
         * 插入数据测试
         */
        Button addData = (Button) findViewById(R.id.add_data);
        assert addData != null;
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                //开始组装第一条数据
                values.put("name", "毛主席语录");
                values.put("author", "毛泽东");
                values.put("pages", 454);
                values.put("price", 25.8);
                //插入第一条数据
                db.insert("Book", null, values);
                values.clear();
                //准备第二条数据
                values.put("name", "邓小平文集");
                values.put("author", "邓小平");
                values.put("pages", 4560);
                values.put("price", 68.9);
                //插入第二条数据
                db.insert("Book", null, values);
            }
        });
        /**
         * 更新数据测试
         */
        Button updateData = (Button) findViewById(R.id.update_data);
        assert updateData != null;
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("price", 9.9);
                db.update("book", values, "name=?", new String[]{"毛主席语录"});
            }
        });
        /**
         * 删除数据测试
         */
        Button deleteData = (Button) findViewById(R.id.delete_data);
        assert deleteData != null;
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Book", "pages>?", new String[]{"1000"});
            }
        });
        /**
         * 查询数据测试
         */
        Button queryData = (Button) findViewById(R.id.query_data);
        assert queryData != null;
        queryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //查询Book表中的所有数据
                Cursor cursor = db.query("Book", null, null, null, null, null, null);
                //如果成功能够移动到结果集首行
                if (cursor.moveToFirst()) {
                    //对结果集进行遍历
                    do {
                        /**
                         * cursor.getXXX方法：获取结果集中某个列的值，参数为列的下标
                         * cursor.getColumnIndex(value)方法：根据列名获取这个列的下标
                         */
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));
                        Log.d("MainActivity", "书名：" + name);
                        Log.d("MainActivity", "作者名：" + author);
                        Log.d("MainActivity", "页数：" + pages);
                        Log.d("MainActivity", "价格：" + price);
                    } while (cursor.moveToNext());
                }
            }
        });
        /**
         * 替换数据（事务管理）测试
         */
        Button replaceData = (Button) findViewById(R.id.replace_data);
        assert replaceData != null;
        replaceData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //开启事务
                db.beginTransaction();
                try {
                    db.delete("Book", null, null);
                    //手动抛出一个异常，让事务失败
/*                    if (true) {
                        throw new NullPointerException();
                    }*/
                    ContentValues values = new ContentValues();
                    values.put("name", "第一行代码");
                    values.put("author", "大表哥");
                    values.put("pages", "1");
                    values.put("price", "2.5");
                    db.insert("Book", null, values);
                    db.setTransactionSuccessful();//事务已经执行
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //结束事务
                    db.endTransaction();
                }
            }
        });
    }
}
