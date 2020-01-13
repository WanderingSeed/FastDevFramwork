package cn.com.hesc.datautilslibrary;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

/**
 * created by liujunlin on 2018/8/1 10:08
 */
public abstract class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * 构造函数
     * @param context 上下文
     * @param name 数据库名
     * @param version 数据库版本号
     */
    public DatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param name 数据库名
     * @param version 数据库版本号
     * @param errorHandler 数据库错误回调
     */
    public DatabaseHelper(Context context, String name, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, null, version, errorHandler);
    }
}
