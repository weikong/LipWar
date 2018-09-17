package com.king.speak.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreferce保存数据到内存中
 *
 * @author luomin
 */
@SuppressLint({"WorldReadableFiles", "WorldWriteableFiles"})
public class SharePreferceTool {


    private SharedPreferences shareprefece;
    private SharedPreferences.Editor editor;
    public static final String LipWar = "LipWar";

    public static String DeaufaltTranslateLanguage = "TranslateLanguage";

    /**
     * Construct
     */
    @SuppressLint("WrongConstant")
    private SharePreferceTool(Context context) {
        // Preferences对象
        shareprefece = context.getSharedPreferences(LipWar,
                Context.MODE_MULTI_PROCESS +
                        Context.MODE_APPEND +
                        Context.MODE_PRIVATE
        );
        editor = shareprefece.edit();
    }

    /**
     * Construct
     */
    @SuppressLint("WrongConstant")
    private SharePreferceTool(Context context, String name) {
        // Preferences对象
        shareprefece = context.getSharedPreferences(name,
                Context.MODE_MULTI_PROCESS +
                        Context.MODE_APPEND +
                        Context.MODE_PRIVATE
        );
        editor = shareprefece.edit();
    }

    /**
     * 获取单例 Create at 2013-6-17
     *
     * @param context
     * @return SharePreferce
     * @author luomin
     */
    public static SharePreferceTool getInstance(Context context) {
        return new SharePreferceTool(context);
    }

    public static SharePreferceTool getInstance(Context context, String name) {
        return new SharePreferceTool(context, name);
    }

    public boolean isEmpty(String key) {
        return !shareprefece.contains(key);
    }

    /**
     * 清理缓存 Create at 2013-7-1
     */
    public void clearCache() {
        editor.clear();
        editor.commit();
    }

    /**
     * 设置SharedPrefere缓存 Create at 2013-6-17
     *
     * @param key   key 键值
     * @param value value 缓存内容
     */
    public boolean setCache(String key, Object value) {
        if (value instanceof Boolean)// 布尔对象
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof String)// 字符串
            editor.putString(key, (String) value);
        else if (value instanceof Integer)// 整型数
            editor.putInt(key, (Integer) value);
        else if (value instanceof Long)// 长整型
            editor.putLong(key, (Long) value);
        else if (value instanceof Float)// 浮点数
            editor.putFloat(key, (Float) value);
        return editor.commit();
    }

    public void delete(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 读取缓存中的字符串 Create at 2013-6-17
     *
     * @param key
     * @return String
     */
    public String getString(String key) {
        return shareprefece.getString(key, "");
    }

    /**
     * 读取缓存中的字符串 Create at 2013-6-17
     *
     * @param key
     * @return String
     */
    public String getString(String key, String default_value) {
        return shareprefece.getString(key, "");
    }

    /**
     * 读取缓存中的布尔型缓存 Create at 2013-6-17
     *
     * @param key
     * @return boolean
     */
    public boolean getBoolean(String key) {
        return shareprefece.getBoolean(key, false);
    }

    /**
     * 读取缓存中的整型数 Create at 2013-6-17
     *
     * @param key
     * @return int
     */
    public int getInt(String key) {
        return shareprefece.getInt(key, 0);
    }

    /**
     * 读取缓存中的长整型数 Create at 2013-6-17
     *
     * @param key
     * @return long
     */
    public long getLong(String key) {
        return shareprefece.getLong(key, 0);
    }

    /**
     * 读取缓存中的长整型数 Create at 2013-6-17
     *
     * @param key
     * @return long
     */
    public long getLong(String key, long default_value) {
        return shareprefece.getLong(key, default_value);
    }

    /**
     * 读取缓存中的浮点数 Create at 2013-6-17
     *
     * @param key
     * @return float
     */
    public float getFloat(String key) {
        return shareprefece.getFloat(key, 0.0f);
    }

    /**
     * 判断是否有缓存
     *
     * @param string
     * @return
     */
    public boolean ifHaveShare(String string) {
        return shareprefece.contains(string);
    }

}
