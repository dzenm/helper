package com.dzenm.helper.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * @author dzenm
 * @date 2019-07-27 17:10
 */
public class SPHelper {

    /************************************* 需要在Application中初始化 *********************************/
    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static SPHelper sSPHelper;

    private SPHelper() {
    }

    public static SPHelper getInstance() {
        if (sSPHelper == null) synchronized (SPHelper.class) {
            if (sSPHelper == null) sSPHelper = new SPHelper();
        }
        return sSPHelper;
    }

    /************************************* 方式一：自定义SharedPreferences文件名 *********************************/

    /**
     * 初始化，建议放在Application
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
    }

    /**
     * 获取SharedPreference文件
     *
     * @param sp SharedPreferences文件名
     * @return
     */
    private SharedPreferences getSharedPreferences(String sp) {
        return mContext.getSharedPreferences(sp, Context.MODE_PRIVATE);
    }

    /**
     * 存数据
     *
     * @param sp    SharedPreferences文件名
     * @param key   存储对象的key
     * @param value 存储对象的值
     */
    public boolean put(String sp, String key, Object value) {
        SharedPreferences.Editor editor = getSharedPreferences(sp).edit();
        if (value instanceof String) {
            editor.putString(key, String.valueOf(value));
        } else if (value instanceof Integer) {
            editor.putInt(key, (int) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (boolean) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        }
        return editor.commit();
    }

    /**
     * 取一个数据
     *
     * @param sp       SharedPreferences文件名
     * @param key      获取对象的key
     * @param defValue 获取数据的默认值（当key不存在时）
     * @return
     */
    public Object get(String sp, String key, Object defValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(sp);
        if (defValue instanceof String) {
            return sharedPreferences.getString(key, String.valueOf(defValue));
        } else if (defValue instanceof Integer) {
            return sharedPreferences.getInt(key, (int) defValue);
        } else if (defValue instanceof Long) {
            return sharedPreferences.getLong(key, (long) defValue);
        } else if (defValue instanceof Float) {
            return sharedPreferences.getFloat(key, (float) defValue);
        } else if (defValue instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (boolean) defValue);
        } else if (defValue instanceof Set) {
            return sharedPreferences.getStringSet(key, (Set<String>) defValue);
        }
        return null;
    }

    /**
     * 移除某个key对应的值
     *
     * @param sp  需要操作的SharedPreference文件名
     * @param key 需要移除的key
     */
    public void remove(String sp, String key) {
        getSharedPreferences(sp)
                .edit()
                .remove(key)
                .apply();
    }

    /**
     * 返回所有的键值对
     *
     * @param sp 需要操作的SharedPreference文件名
     * @return
     */
    public Map<String, ?> getAll(String sp) {
        return getSharedPreferences(sp).getAll();
    }

    /**
     * 是否存在该键值对
     *
     * @param sp  需要操作的SharedPreference文件名
     * @param key 需要查询是否存在的键
     * @return
     */
    public boolean contains(String sp, String key) {
        return getSharedPreferences(sp).contains(key);
    }

    /**
     * 清除所有数据
     *
     * @param sp 需要操作的SharedPreference文件名
     */
    @SuppressLint("CommitPrefEdits")
    public void clear(String sp) {
        getSharedPreferences(sp)
                .edit()
                .clear();
    }

    /************************************* 方式二：初始化一个文件名 *********************************/

    private SharedPreferences mSharedPreferences;   // 全局SharedPreferences
    private SharedPreferences.Editor mEditor;       // 全局Editor

    /**
     * 初始化，建议放在Application
     *
     * @param context
     * @param sp      初始化的文件名
     */
    @SuppressLint("CommitPrefEdits")
    public void init(Context context, String sp) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(sp, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    /**
     * 存一个数据
     *
     * @param key   存储对象的key
     * @param value 存储对象的值
     */
    public boolean put(String key, Object value) {
        if (value instanceof String) {
            mEditor.putString(key, String.valueOf(value));
        } else if (value instanceof Integer) {
            mEditor.putInt(key, (int) value);
        } else if (value instanceof Long) {
            mEditor.putLong(key, (long) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (float) value);
        } else if (value instanceof Boolean) {
            mEditor.putBoolean(key, (boolean) value);
        } else if (value instanceof Set) {
            mEditor.putStringSet(key, (Set<String>) value);
        }
        return mEditor.commit();
    }

    /**
     * 取一个数据
     *
     * @param key      获取对象的key
     * @param defValue 获取数据的默认值（当key不存在时）
     * @return
     */
    public Object get(String key, Object defValue) {
        if (defValue instanceof String) {
            return mSharedPreferences.getString(key, String.valueOf(defValue));
        } else if (defValue instanceof Integer) {
            return mSharedPreferences.getInt(key, (int) defValue);
        } else if (defValue instanceof Long) {
            return mSharedPreferences.getLong(key, (long) defValue);
        } else if (defValue instanceof Float) {
            return mSharedPreferences.getFloat(key, (float) defValue);
        } else if (defValue instanceof Boolean) {
            return mSharedPreferences.getBoolean(key, (boolean) defValue);
        } else if (defValue instanceof Set) {
            return mSharedPreferences.getStringSet(key, (Set<String>) defValue);
        }
        return null;
    }


    /**
     * 移除某个key对应的值
     *
     * @param key 需要移除的key
     */
    public void remove(String key) {
        mEditor.remove(key)
                .apply();
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    /**
     * 是否存在该键值对
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        mEditor.clear();
    }
}
