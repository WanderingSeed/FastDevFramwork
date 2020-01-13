package cn.com.hesc.datautilslibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库工具类
 * 采用系统原生API，所以速度更快
 * created by liujunlin on 2018/8/1 10:02
 */
public class DataBaseUtils {

    private Context mContext;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SQLiteDatabase rDb;

    public DataBaseUtils(@NonNull Context context,@NonNull DatabaseHelper helper){
        mContext = context;
        mDbHelper = helper;
    }

    /**
     * 开启数据库
     * @return
     */
    public DataBaseUtils open(){
        mDb = mDbHelper.getWritableDatabase();
        rDb = mDbHelper.getReadableDatabase();
        return this;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * 创建表
     * 为了实现数据库的兼容性问题，所有列默认都使用String类型
     * @param tableName 表名
     * @param columns 列名
     */
    public boolean createTable(String tableName,String[] columns){

        if(tabIsExist(tableName))
            return true;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("create table ");
            stringBuffer.append(tableName);
            stringBuffer.append("(");
            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                stringBuffer.append(column);
                stringBuffer.append(" Text");
                if(i < columns.length - 1)
                    stringBuffer.append(",");
                else
                    stringBuffer.append(")");
            }
            mDb.execSQL(stringBuffer.toString());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除表
     * @param tableName 表名
     * @return
     */
    public boolean dropTable(String tableName){
        if(!tabIsExist(tableName))
            return true;
        try {
            mDb.execSQL("DROP TABLE IF EXISTS "+tableName);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 清空表数据
     * @param tableName 表名
     * @return
     */
    public boolean clearTable(String tableName){
        if(!tabIsExist(tableName))
            return true;
        try {
            mDb.execSQL("delete from "+tableName);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 执行SQL语句，不需要返回值
     *
     * @param sql
     */
    public void exeSql(String sql) {
        mDb.execSQL(sql);
    }

    /**
     * 插入数据（增）
     *
     * @param tablename
     *            :表名
     * @param colname
     *            :列名
     * @param colvalues
     *            :列值
     *  返回插入表的索引值 -1表示出错
     */
    public long insertItem(@NonNull String tablename,@NonNull String[] colname,@NonNull String[] colvalues) {
        long rowid = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (int i = 0; i < colname.length; i++) {
                initialValues.put(colname[i], colvalues[i].toString());
            }
            rowid = mDb.insert(tablename, null, initialValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }

    /**
     * 插入数据（增）
     * @param tablename 表名
     * @param map key表字段value插入的值
     * @return 返回插入表的索引值 -1表示出错
     */
    public long insertItem(@NonNull String tablename,@NonNull Map<String,String> map) {
        long rowid = -1;
        try {
            ContentValues initialValues = new ContentValues();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                initialValues.put(entry.getKey(), entry.getValue());
            }
            rowid = mDb.insert(tablename, null, initialValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }

    /**
     * 删除表的数据
     *
     * @param tablename
     *            表名
     * @param whereclause
     *            条件列名
     * @param wherestr
     *            条件列名值
     * 返回-1表示异常，0表示没删成功
     */
    public long deleteItem(String tablename, String[] whereclause,
                            String[] wherestr) {
        long rowid = 0;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < whereclause.length; i++) {
                String column = whereclause[i];
                String cv = wherestr[i];
                if(TextUtils.isEmpty(cv)){
                    cv = "";
                }
                stringBuffer.append(column);
                stringBuffer.append("=");
                stringBuffer.append("'");
                stringBuffer.append(cv);
                stringBuffer.append("'");
                if(i < whereclause.length - 1){
                    stringBuffer.append(" and ");
                }
            }
            rowid = mDb.delete(tablename, stringBuffer.toString(), null);
        } catch (Exception e) {
            rowid = -1;
            e.printStackTrace();
        }
        return rowid;
    }

    /**
     * 删除表的数据
     * @param tablename 表名
     * @param map 条件值(key列表，value 值)
     * @return 返回-2表示异常，-1表示没删成功
     */
    public long deleteItem(String tablename, Map<String,String> map) {
        long rowid = -1;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            int index = 1;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String column = entry.getKey();
                String cv = entry.getValue();
                if(TextUtils.isEmpty(cv))
                    cv = "";
                stringBuffer.append(column);
                stringBuffer.append("=");
                stringBuffer.append("'");
                stringBuffer.append(cv);
                stringBuffer.append("'");
                if(index < map.size()){
                    stringBuffer.append(" and ");
                }
                index++;
            }
            rowid = mDb.delete(tablename, stringBuffer.toString(), null);
        } catch (Exception e) {
            rowid = -2;
            e.printStackTrace();
        }
        return rowid;
    }

    /**
     * 修改表
     *
     * @param tablename
     *            :表名
     * @param colname
     *            :列名
     * @param colvalues
     *            :列值
     * @param whereclause
     *            :条件列名
     * @param wherestr
     *            :条件列名值
     *  返回-1表示更新失败
     */
    public long updateDiary(String tablename, String[] colname,
                            String[] colvalues, String[] whereclause, String[] wherestr) {
        long rowid = -1;
        try {
            ContentValues args = new ContentValues();
            for (int i = 0; i < colname.length; i++) {
                args.put(colname[i], colvalues[i]);
            }
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < whereclause.length; i++) {
                String column = whereclause[i];
                String cv = wherestr[i];
                if(TextUtils.isEmpty(cv)){
                    cv = "";
                }
                stringBuffer.append(column);
                stringBuffer.append("=");
                stringBuffer.append("'");
                stringBuffer.append(cv);
                stringBuffer.append("'");
                if(i < whereclause.length - 1){
                    stringBuffer.append(" and ");
                }
            }
            rowid = mDb.update(tablename, args, stringBuffer.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowid;
    }

    /**
     * 更新数据
     * @param tablename 表名
     * @param updateStr 要更新的内容（key列表 value列值）
     * @param whereclause 条件组合
     * @return -1更新失败  -2数据异常 >0更新的列索引
     */
    public long updateDiary(String tablename, Map<String,String> updateStr, Map<String,String> whereclause) {
        long rowid = -1;
        try {
            ContentValues args = new ContentValues();
            for (Map.Entry<String, String> entry : updateStr.entrySet()) {
                String column = entry.getKey();
                String cv = entry.getValue();
                if(TextUtils.isEmpty(cv))
                    cv = "";
                args.put(column, cv);
            }

            StringBuffer stringBuffer = new StringBuffer();
            int index = 1;
            for (Map.Entry<String, String> entry : whereclause.entrySet()) {
                String column = entry.getKey();
                String cv = entry.getValue();
                if(TextUtils.isEmpty(cv))
                    cv = "";
                stringBuffer.append(column);
                stringBuffer.append("=");
                stringBuffer.append("'");
                stringBuffer.append(cv);
                stringBuffer.append("'");
                if(index < whereclause.size()){
                    stringBuffer.append(" and ");
                }
                index++;
            }
            rowid = mDb.update(tablename, args, stringBuffer.toString(), null);
        } catch (Exception e) {
            rowid = -2;
            e.printStackTrace();
        }
        return rowid;
    }

    /**
     * 查询表里某些字段集合--select id,name from table效果
     * @param tableName 表名
     * @param columns 要查询的列名
     * @param whereclause 条件
     * @param orderBy 排序的列名 可为空
     * @param isAsc true 升序  false降序
     * @return
     */
    public List<Map<String,String>> queryColumn(String tableName, String[] columns,@Nullable Map<String,String> whereclause, @Nullable String orderBy, boolean isAsc){
        List<Map<String,String>> items = new ArrayList<>();
        try {
            StringBuffer stringBuffer = new StringBuffer();
            if(whereclause != null){
                int index = 1;
                for (Map.Entry<String, String> entry : whereclause.entrySet()) {
                    String column = entry.getKey();
                    String cv = entry.getValue();
                    if(TextUtils.isEmpty(cv))
                        cv = "";
                    stringBuffer.append(column);
                    stringBuffer.append("=");
                    stringBuffer.append("'");
                    stringBuffer.append(cv);
                    stringBuffer.append("'");
                    if(index < whereclause.size()){
                        stringBuffer.append(" and ");
                    }
                    index++;
                }
            }

            StringBuffer order = new StringBuffer();
            if(orderBy!=null){
                order.append(orderBy);
                if(!isAsc){
                    order.append(" desc ");
                }
            }
            Cursor mCursor = mDb.query(true, tableName, columns, whereclause == null?null:stringBuffer.toString(), null, null, null, orderBy==null?null:order.toString(), null);
            if (mCursor != null && mCursor.getCount() > 0){
                mCursor.moveToFirst();
                while (!mCursor.isAfterLast()){
                    Map<String,String> result = new HashMap<>();
                    for (int i = 0; i < columns.length; i++) {
                        String column = columns[i];
                        result.put(column,mCursor.getString(mCursor.getColumnIndex(column)));
                    }
                    items.add(result);
                    mCursor.moveToNext();
                }
                mCursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 返回表数据所有列--select * from table 效果
     * @param tableName 表名
     * @param whereclause 条件
     * @param orderBy 排序的列名 可为空
     * @param isAsc true 升序  false降序
     * @return 数据列表
     * 不建议使用，数据量大的话，搜索所有列较慢，建议只取需要的列
     */
    public List<Map<String,String>> queryAllColumn(String tableName,@Nullable Map<String,String> whereclause, @Nullable String orderBy, boolean isAsc){
        List<Map<String,String>> items = new ArrayList<>();
        try {
            StringBuffer stringBuffer = new StringBuffer();
            if(whereclause != null){
                int index = 1;
                for (Map.Entry<String, String> entry : whereclause.entrySet()) {
                    String column = entry.getKey();
                    String cv = entry.getValue();
                    if(TextUtils.isEmpty(cv))
                        cv = "";
                    stringBuffer.append(column);
                    stringBuffer.append("=");
                    stringBuffer.append("'");
                    stringBuffer.append(cv);
                    stringBuffer.append("'");
                    if(index < whereclause.size()){
                        stringBuffer.append(" and ");
                    }
                    index++;
                }
            }

            StringBuffer order = new StringBuffer();
            if(orderBy!=null){
                order.append(orderBy);
                if(!isAsc){
                    order.append(" desc ");
                }
            }
            Cursor mCursor = mDb.query(true, tableName, null, whereclause == null?null:stringBuffer.toString(), null, null, null, orderBy==null?null:order.toString(), null);
            if (mCursor != null && mCursor.getCount() > 0){
                mCursor.moveToFirst();
                String[] columns = mCursor.getColumnNames();
                while (!mCursor.isAfterLast()){
                    Map<String,String> result = new HashMap<>();
                    for (int i = 0; i < columns.length; i++) {
                        String column = columns[i];
                        result.put(column,mCursor.getString(mCursor.getColumnIndex(column)));
                    }
                    items.add(result);
                    mCursor.moveToNext();
                }
                mCursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 分页查询表里某些字段集合--select id,name from table效果
     * @param tableName 表名
     * @param columns 要查询的列名
     * @param whereclause 条件
     * @param pageindex 页索引，从1 开始
     * @param pageofnumber 每页条数
     * @return
     */
    public List<Map<String,String>> queryColumnOfPage(String tableName, String[] columns,@Nullable Map<String,String> whereclause, int pageindex, int pageofnumber){
        List<Map<String,String>> items = new ArrayList<>();
        try {
            StringBuffer stringBuffer = new StringBuffer();
            if(whereclause != null){
                int index = 1;
                for (Map.Entry<String, String> entry : whereclause.entrySet()) {
                    String column = entry.getKey();
                    String cv = entry.getValue();
                    if(TextUtils.isEmpty(cv))
                        cv = "";
                    stringBuffer.append(column);
                    stringBuffer.append("=");
                    stringBuffer.append("'");
                    stringBuffer.append(cv);
                    stringBuffer.append("'");
                    if(index < whereclause.size()){
                        stringBuffer.append(" and ");
                    }
                    index++;
                }
            }

            String limit = (pageindex-1)+","+pageofnumber;
            Cursor mCursor = mDb.query(true, tableName, columns, whereclause == null?null:stringBuffer.toString(), null, null, null, null, limit);
            if (mCursor != null && mCursor.getCount() > 0){
                mCursor.moveToFirst();
                while (!mCursor.isAfterLast()){
                    Map<String,String> result = new HashMap<>();
                    for (int i = 0; i < columns.length; i++) {
                        String column = columns[i];
                        result.put(column,mCursor.getString(mCursor.getColumnIndex(column)));
                    }
                    items.add(result);
                    mCursor.moveToNext();
                }
                mCursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 分页查询-返回表数据所有列--select * from table 效果
     * @param tableName 表名
     * @param whereclause 条件
     * @param pageindex  页索引
     * @param pageofnumber 每页条数
     * @return 数据列表
     * 不建议使用，数据量大的话，搜索所有列较慢，建议只取需要的列
     */
    public List<Map<String,String>> queryAllColumnOfPage(String tableName, @Nullable Map<String,String> whereclause, int pageindex, int pageofnumber){
        List<Map<String,String>> items = new ArrayList<>();
        try {
            StringBuffer stringBuffer = new StringBuffer();
            if(whereclause != null){
                int index = 1;
                for (Map.Entry<String, String> entry : whereclause.entrySet()) {
                    String column = entry.getKey();
                    String cv = entry.getValue();
                    if(TextUtils.isEmpty(cv))
                        cv = "";
                    stringBuffer.append(column);
                    stringBuffer.append("=");
                    stringBuffer.append("'");
                    stringBuffer.append(cv);
                    stringBuffer.append("'");
                    if(index < whereclause.size()){
                        stringBuffer.append(" and ");
                    }
                    index++;
                }
            }

            String limit = ((pageindex-1)*pageofnumber)+","+pageofnumber;
            Cursor mCursor = mDb.query(true, tableName, null, whereclause==null?null:stringBuffer.toString(), null, null, null, null, limit);
            if (mCursor != null && mCursor.getCount() > 0){
                mCursor.moveToFirst();
                String[] columns = mCursor.getColumnNames();
                while (!mCursor.isAfterLast()){
                    Map<String,String> result = new HashMap<>();
                    for (int i = 0; i < columns.length; i++) {
                        String column = columns[i];
                        result.put(column,mCursor.getString(mCursor.getColumnIndex(column)));
                    }
                    items.add(result);
                    mCursor.moveToNext();
                }
                mCursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 判断表是否存在
     *
     * @author liujunlin
     * @param tabName
     * @return true表存在
     */
    public boolean tabIsExist(String tabName) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        Cursor cursor = null;
        try {

            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
                    + tabName.trim() + "' ";
            cursor = rDb.rawQuery(sql, null);

            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
                cursor.close();
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询表里的数据执行sql语句
     * 注意sql字符串不要以";"结尾
     * 返回
     */
    public Cursor getSomedata(String sql) {
        if(sql.endsWith(";"))
            sql = sql.substring(0,sql.length());
        Cursor mCursor = null;
        try {
            mCursor = rDb.rawQuery(sql, null);
            if (mCursor != null && mCursor.getCount() > 0)
                mCursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mCursor;
    }

    /**
     * 查询数据总量
     * @param tableName 表名
     * @return 数据总量
     */
    public int getCount(String tableName,@Nullable Map<String,String> whereclause){
        int result = 0;
        if(whereclause == null){
            Cursor cursor = null;
            cursor = getSomedata("select count(*) from "+tableName);
            if(cursor!=null){
                result = cursor.getInt(0);
                cursor.close();
            }
        }else{
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("select count(*) from "+tableName);
            stringBuffer.append(" where ");
            int index = 1;
            for (Map.Entry<String, String> entry : whereclause.entrySet()) {
                String column = entry.getKey();
                String cv = entry.getValue();
                if(TextUtils.isEmpty(cv))
                    cv = "";
                stringBuffer.append(column);
                stringBuffer.append("=");
                stringBuffer.append("'");
                stringBuffer.append(cv);
                stringBuffer.append("'");
                if(index < whereclause.size()){
                    stringBuffer.append(" and ");
                }
                index++;
            }
            Cursor cursor = getSomedata(stringBuffer.toString());
            if(cursor!=null){
                result = cursor.getInt(0);
                cursor.close();
            }
        }
        return result;
    }


    /**
     * 判断表是否为空
     * @author liujunlin
     * @param tablename  表名
     * @return true 是空表
     */
    public boolean  tableIsEmpty(String tablename){
        boolean result = true;
        if (tablename == null) {
            return false;
        }
        int count = getCount(tablename,null);
        if(count > 0)
            result = false;
        return result;
    }
}
